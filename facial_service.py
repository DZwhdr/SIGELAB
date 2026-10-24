from fastapi import FastAPI, File, UploadFile
import face_recognition
import uvicorn
import io

app = FastAPI()

@app.post("/verify")
async def verify_face(
    image_cam: UploadFile = File(...), 
    image_db: UploadFile = File(...)
):
    try:
        # Cargar la foto tomada en el momento (cámara)
        cam_bytes = await image_cam.read()
        cam_img = face_recognition.load_image_file(io.BytesIO(cam_bytes))
        cam_encodings = face_recognition.face_encodings(cam_img)

        # Cargar la foto registrada del usuario en el sistema
        db_bytes = await image_db.read()
        db_img = face_recognition.load_image_file(io.BytesIO(db_bytes))
        db_encodings = face_recognition.face_encodings(db_img)

        # Verificar que ambos rostros hayan sido detectados
        if not cam_encodings or not db_encodings:
            return {"success": False, "message": "No se detectó un rostro en alguna de las imágenes."}

        # Comparar si ambas fotos pertenecen a la misma persona
        match = face_recognition.compare_faces([db_encodings[0]], cam_encodings[0])[0]

        return {"success": bool(match)}

    except Exception as e:
        return {"success": False, "message": str(e)}

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)