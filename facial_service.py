from fastapi import FastAPI, File, UploadFile
import face_recognition
import uvicorn
import io
import os
import tempfile

app = FastAPI()

@app.post("/verify")
async def verify_face(
        image_cam: UploadFile = File(...),
        image_db: UploadFile = File(...)
):
    try:
        print("\n--- NUEVA VERIFICACIÓN ---")

        # Leer los bytes de ambas imágenes
        cam_bytes = await image_cam.read()
        db_bytes = await image_db.read()

        # Crear archivos temporales en disco
        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_cam:
            temp_cam.write(cam_bytes)
            temp_cam_path = temp_cam.name

        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_db:
            temp_db.write(db_bytes)
            temp_db_path = temp_db.name

        try:
            # face_recognition lee directamente del archivo temporal
            cam_img = face_recognition.load_image_file(temp_cam_path)
            cam_encodings = face_recognition.face_encodings(cam_img)

            db_img = face_recognition.load_image_file(temp_db_path)
            db_encodings = face_recognition.face_encodings(db_img)

            print(f"Rostro detectado en cámara: {bool(cam_encodings)}")
            print(f"Rostro detectado en base de datos: {bool(db_encodings)}")

            if not cam_encodings or not db_encodings:
                print("❌ ERROR: Faltó un rostro en alguna de las dos fotos.")
                return {"success": False, "message": "No se detectó un rostro."}

            distancia = face_recognition.face_distance([db_encodings[0]], cam_encodings[0])[0]
            match = face_recognition.compare_faces([db_encodings[0]], cam_encodings[0])[0]

            print(f"Distancia facial: {distancia:.4f}")
            print(f"Resultado final (Coinciden?): {match}")
            print("--------------------------")

            return {"success": bool(match)}

        finally:
            # Limpiar los archivos temporales pase lo que pase
            if os.path.exists(temp_cam_path):
                os.remove(temp_cam_path)
            if os.path.exists(temp_db_path):
                os.remove(temp_db_path)

    except Exception as e:
        print(f"❌ ERROR INTERNO: {str(e)}")
        return {"success": False, "message": str(e)}

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)