package utez.edu.ApiRestEventFlow.validation;

public class ErrorMessages {

    //errors
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String USERS_NOT_FOUND = "No se encontraron usuarios";
    public static final String PHONE_EXIST = "El telefono ya esta registrado";
    public static final String EMAIL_EXIST = "El email ya esta registrado";
    public static final String INVALID_SENT_BY_USER = "El usuario que registra al checador debe ser un administrador";
    public static final String SENT_BY_USER_NOT_FOUND = "Administrador no encontrado";
    public static final String IS_NOT_ADMIM = "El usuario no es un administrador";
    public static final String IS_NOT_CHECKER = "El usuario no es un checador";
    public static final String SAME_PASSWORD = "La nueva contraseña no puede ser igual a la anterior";
    public static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";
    public static final String ACTIVITY_NOT_FOUND = "No se encontro la actividad";
    public static final String ACTIVITIES_NOT_FOUND = "No se encontraron actividades";
    public static final String IS_NOT_EVENT = "La actividad no es un evento";
    public static final String IS_NOT_WORKSHOP = "La actividad no es un evento";
    //sucessful
    public static final String SUCCESSFUL_REGISTRATION = "Regitro exitoso";
    public static final String SUCCESFUL_UPDATE = "Actualizacion exitosa";
    public static final String SUCCESFUL_CHANGE_STATUS = "Se ha actualizado el estado a ";
    public static final String SUCCESSFUL_PASSWORD_UPDATE = "Contraseña actualizada exitosamente";

    //dto
    public static final String ID_REQUIRED = "El ID es obligatorio";
    public static final String NAME_REQUIRED = "El nombre es obligatorio";
}
