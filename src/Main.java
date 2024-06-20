import Excepcion.NoHayKitsException;
import Excepcion.TemperaturaAltaException;
import Modelo.Persona;
import Modelo.RegistroTemperatura;
import Modelo.SSMSystem;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        SSMSystem sistema = new SSMSystem(5); // Inicializamos con 3 kits disponibles

        try {
            Persona p1 = new Persona("Ignacio", "Fuentes", 30, "Centro", "12345678", "Medico");
            sistema.registrarPersona(p1);

            Persona p2 = new Persona("Juliana", "Gomez", 25, "Serena", "87654321", "Enfermera");
            sistema.registrarPersona(p2);

            Persona p3 = new Persona("lucas", "Martinez", 40, "Sierras", "12344321", "Policia");
            sistema.registrarPersona(p3);

            Persona p4 = new Persona("María", "López", 35, "Colinas", "56789012", "Maestra");
            sistema.registrarPersona(p4);

            Persona p5 = new Persona("Pedro", "Pedro", 28, "Cerrito", "90123456", "Ingeniero");
            sistema.registrarPersona(p5);

            // Intentamos registrar una persona con DNI repetido (p2)
            Persona p6 = new Persona("Juliana", "Gomez", 25, "Serena", "87654321", "Enfermera");
            sistema.registrarPersona(p6); // Debería mostrar mensaje de error por DNI repetido

            // Realizamos el testeo de temperatura
            sistema.testear();

            // Aislamos personas con temperatura alta
            sistema.aislar();


        } catch (NoHayKitsException e) {
            System.out.println(e.getMessage());

            // Preguntamos si hay más kits disponibles
            // Simulamos que el SSM provee más kits
            sistema.agregarKits(2);

            // Intentamos registrar nuevamente (p6)
            try {
                Persona p7 = new Persona("Carlos", "Rodriguez", 32, "Norte", "87654321", "Bombero");
                sistema.registrarPersona(p7);
            } catch (NoHayKitsException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (TemperaturaAltaException e) {
            System.out.println(e.getMessage());
        }

        // Atendiendo personas registradas
        System.out.println("\nAtendiendo personas registradas:");
        while (!sistema.getColaPersonas().isEmpty()) {
            Persona personaAtendida = sistema.atenderSiguientePersona();
            System.out.println("Persona atendida: " + personaAtendida);
        }

        // Mostrando tabla de resultados de temperatura
        System.out.println("\nTabla de resultados de temperatura:");
        for (Map.Entry<Integer, RegistroTemperatura> entry : sistema.getTablaResultados().entrySet()) {
            System.out.println("Kit " + entry.getKey() + ": " + entry.getValue());
        }

        // Mostrando personas sanas y para aislar
        System.out.println("\nPersonas sanas:");
        for (Persona persona : sistema.getPersonasSanas()) {
            System.out.println(persona);
        }

        System.out.println("\nPersonas para aislar:");
        for (Persona persona : sistema.getPersonasAislar()) {
            System.out.println(persona);
        }
        // Generamos el JSON con los resultados
        sistema.generarJSON();
    }
}
