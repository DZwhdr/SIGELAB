package com.sigelab.model;

// Clase que representa el modelo de Rol para clasificar a los usuarios en el sistema (ej. ADMIN, ENCARGADO)
public class Rol {

    // Atributos principales que coinciden con la tabla de roles en la base de datos
    private int id;          // Identificador único del rol
    private String nombre;   // Nombre o etiqueta del rol (ADMIN, ENCARGADO, etc.)
    private String descripcion; // Breve explicación de lo que puede hacer este rol

    // Constructor vacío por si necesitamos inicializar el objeto sin datos y llenarlo después
    public Rol() {
    }

    // Constructor completo para inicializar el rol de un solo golpe con sus datos
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Métodos Getters y Setters para obtener (get) y modificar (set) los valores de los atributos privados
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Método toString personalizado: hace que al mostrar el rol en un menú desplegable (JComboBox),
    // aparezca directamente el nombre del rol en lugar de letras revueltas de memoria de Java.
    @Override
    public String toString() {
        return nombre;
    }
package com.sigelab.model;

// Clase que representa el modelo de Rol para clasificar a los usuarios en el sistema (ej. ADMIN, ENCARGADO)
public class Rol {

    // Atributos principales que coinciden con la tabla de roles en la base de datos
    private int id;          // Identificador único del rol
    private String nombre;   // Nombre o etiqueta del rol (ADMIN, ENCARGADO, etc.)
    private String descripcion; // Breve explicación de lo que puede hacer este rol

    // Constructor vacío por si necesitamos inicializar el objeto sin datos y llenarlo después
    public Rol() {
    }

    // Constructor completo para inicializar el rol de un solo golpe con sus datos
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Métodos Getters y Setters para obtener (get) y modificar (set) los valores de los atributos privados
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Método toString personalizado: hace que al mostrar el rol en un menú desplegable (JComboBox),
    // aparezca directamente el nombre del rol en lugar de letras revueltas de memoria de Java.
    @Override
    public String toString() {
        return nombre;
    }
}