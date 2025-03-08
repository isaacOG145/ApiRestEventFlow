package utez.edu.ApiRestEventFlow.validation;

public class ErrorMessages {

    //errors
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String USERS_NOT_FOUND = "No se encontraron usuarios";
    public static final String PHONE_EXIST = "El telefono ya esta registrado";
    public static final String EMAIL_EXIST = "El email ya esta registrado";
    public static final String EXCEPTION = "Ha ocurrido un error inesperado";
    public static final String INVALID_SENT_BY_USER = "El usuario que registra al checador debe ser un administrador";
    public static final String SENT_BY_USER_NOT_FOUND = "Administrador no encontrado";
    public static final String SAME_PASSWORD = "La nueva contraseña no puede ser igual a la anterior";
    public static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";
    //sucessful
    public static final String SUCCESSFUL_REGISTRATION = "Regitro exitoso";
    public static final String SUCCESFUL_UPDATE = "Actualizacion exitosa";
    public static final String SUCCESFUL_CHANGE_STATUS = "Se ha actualizado el estado a ";
    public static final String SUCCESSFUL_PASSWORD_UPDATE = "Contraseña actualizada exitosamente";
}
