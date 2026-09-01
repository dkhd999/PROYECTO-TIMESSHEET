package modelo;

public class ValidarCedula {

    public static boolean validarCedulaEcuatoriana(String cedula) {
        // 1. Validar longitud y que sean números
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }

        try {
            // 2. Validar provincia (2 primeros dígitos)
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) {
                return false;
            }

            // 3. Validar tercer dígito (debe ser menor a 6 para personas naturales)
            int tercerDigito = Character.getNumericValue(cedula.charAt(2));
            if (tercerDigito >= 6) {
                return false;
            }

            // 4. Algoritmo Modulo 10
            int suma = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(cedula.charAt(i));
                if (i % 2 == 0) {
                    digito *= 2;
                    if (digito > 9) digito -= 9;
                }
                suma += digito;
            }

            int verificador = 10 - (suma % 10);
            if (verificador == 10) verificador = 0;

            return verificador == Character.getNumericValue(cedula.charAt(9));
            
        } catch (Exception e) {
            return false;
        }
        
    }
    
}