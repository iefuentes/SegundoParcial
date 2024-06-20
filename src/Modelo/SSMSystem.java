package Modelo;

import Excepcion.NoHayKitsException;
import Excepcion.TemperaturaAltaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SSMSystem {
    private Queue<Persona> colaPersonas;
    private int kitsDisponibles;
    private int contadorDeKits;
    private Map<Integer, RegistroTemperatura> tablaResultados;
    private List<Persona> personasSanas;
    private List<Persona> personasAislar;

    public SSMSystem(int kitsIniciales) {
        this.colaPersonas = new LinkedList<>();
        this.kitsDisponibles = kitsIniciales;
        this.contadorDeKits = 0;
        this.tablaResultados = new HashMap<>();
        this.personasSanas = new ArrayList<>();
        this.personasAislar = new ArrayList<>();
    }

    public void registrarPersona(Persona persona) throws NoHayKitsException {
        // Verificar si ya existe una persona con el mismo DNI
        for (Persona p : colaPersonas) {
            if (p.getDni().equals(persona.getDni())) {
                System.out.println("Error: El DNI " + persona.getDni() + " ya está registrado.");
                return; // Salir del método si el DNI está repetido
            }
        }

        // Si no hay kits disponibles, lanzar excepción
        if (kitsDisponibles <= 0) {
            throw new NoHayKitsException("No hay kits disponibles para realizar el test.");
        }

        // Asignar número de kit y registrar la persona
        kitsDisponibles--;
        contadorDeKits++;
        persona.setNumeroDeKit(contadorDeKits);
        colaPersonas.add(persona);
        System.out.println("Persona registrada con éxito. Kit asignado: " + contadorDeKits);
    }

    public void testear() {
        System.out.println("\nRealizando test de temperatura:");

        Random random = new Random();
        for (Persona persona : colaPersonas) { //buscamos en toda los pacientes ingresados esperando para testear
            double temperatura = 36 + random.nextDouble() * (39 - 36); //generamos una temperatura random
            RegistroTemperatura registro = new RegistroTemperatura(persona.getDni(), temperatura); //generamos un nuevo registro con el dni y la temperatura de la perssna
            tablaResultados.put(persona.getNumeroDeKit(), registro); //agregamos la persona con su numero de kit como clave y con su registro de temperatura y dni
            System.out.println("Kit " + persona.getNumeroDeKit() + ": DNI " + persona.getDni() + ", Temperatura: " + temperatura + " °C");

            // Evaluamos si la persona debe ser aislada
            if (temperatura >= 38) {
                personasAislar.add(persona); //se agrega a las personas para aislar
            } else {
                personasSanas.add(persona); //se agrega a las personas sanas
            }
        }
    }

    public void aislar() throws TemperaturaAltaException {
        System.out.println("\nAislamiento de personas con temperatura alta:");

        for (Persona persona : colaPersonas) {
            RegistroTemperatura registro = tablaResultados.get(persona.getNumeroDeKit());
            if (registro != null && registro.getTemperatura() >= 38) { //vemos que el registro no este vacio y que la temperatura sea mayor a 38
                throw new TemperaturaAltaException("Persona aislada: Kit " + persona.getNumeroDeKit() + ", Barrio: " + persona.getBarrio());
            }
        }

        System.out.println("Ninguna persona requiere aislamiento por temperatura alta.");
    }

    public Persona atenderSiguientePersona() { //movemos uno en la fila
        return colaPersonas.poll();
    }

    public int getKitsDisponibles() {
        return kitsDisponibles;
    }

    public Queue<Persona> getColaPersonas() {
        return colaPersonas;
    }

    public Map<Integer, RegistroTemperatura> getTablaResultados() {
        return tablaResultados;
    }

    public List<Persona> getPersonasSanas() {
        return personasSanas;
    }

    public List<Persona> getPersonasAislar() {
        return personasAislar;
    }

    public void generarJSON() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, List<Object>> datos = new HashMap<>();
        List<Object> sanos = new ArrayList<>(); //array de sanos
        List<Object> aislar = new ArrayList<>(); //array de aislados

        // Agregar personas sanas al arreglo "sanos"
        for (Persona persona : personasSanas) { //sacamos la informacoin de cada persona sana para agregarlo a un hashmap, que luego se agrega al array
            Map<String, Object> infoPersona = new HashMap<>();
            infoPersona.put("nombre", persona.getNombre());
            infoPersona.put("apellido", persona.getApellido());
            infoPersona.put("edad", persona.getEdad());
            infoPersona.put("barrio", persona.getBarrio());
            infoPersona.put("dni", persona.getDni());
            infoPersona.put("ocupacion", persona.getOcupacion());
            sanos.add(infoPersona);
        }

        // Agregar personas a aislar al arreglo "aislar" con kit, temperatura y barrio
        for (Persona persona : personasAislar) {
            Map<String, Object> infoAislar = new HashMap<>();
            infoAislar.put("kit", persona.getNumeroDeKit());
            RegistroTemperatura registro = tablaResultados.get(persona.getNumeroDeKit());
            if (registro != null) {
                infoAislar.put("temperatura", registro.getTemperatura());
                infoAislar.put("barrio", persona.getBarrio());
            }
            aislar.add(infoAislar);
        }

        // Colocar los arreglos en el objeto principal
        datos.put("sanos", sanos);
        datos.put("aislar", aislar);

        // Convertir a JSON y escribir en archivo
        try {
            mapper.writeValue(new File("resultados.json"), datos); //creo el jason con los datos de ambos array
            System.out.println("\nArchivo JSON generado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo JSON: " + e.getMessage()); //manejo de error por si el archivo no se crea
        }
    }
   //metodo para agregar kits cuando se terminen
    public void agregarKits(int cantidad) {
        this.kitsDisponibles += cantidad;
        System.out.println("Se han agregado " + cantidad + " kits. Kits disponibles ahora: " + kitsDisponibles);
    }
}
