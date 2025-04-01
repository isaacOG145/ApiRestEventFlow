package utez.edu.ApiRestEventFlow.validation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    /**
     * Valida si una fecha es futura.
     *
     * @param date La fecha a validar.
     * @return true si la fecha es futura, false en caso contrario.
     */
    public static boolean isFutureDate(Date date) {
        if (date == null) {
            return false; // O lanza una excepción si prefieres
        }
        LocalDate inputDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return inputDate.isAfter(currentDate);
    }

    /**
     * Valida si una fecha es futura (para LocalDate).
     *
     * @param date La fecha a validar.
     * @return true si la fecha es futura, false en caso contrario.
     */
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) {
            return false; // O lanza una excepción si prefieres
        }
        LocalDate currentDate = LocalDate.now();
        return date.isAfter(currentDate);
    }
}