import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Un objeto de esta clase mantiene una 
 * colección map que asocia  categorías (las claves) con
 * la lista (una colección ArrayList) de cursos que pertenecen a esa categoría 
 * Por ej. una entrada del map asocia la categoría 'BASES DE DATOS"' con
 * una lista de cursos de esa categoría
 * 
 * Las claves en el map se recuperan en orden alfabético y 
 * se guardan siempre en mayúsculas
 * 
 * Ruben Gonzalez Rivera
 */
public class PlataformaCursos
{
    private final String ESPACIO = " ";
    private final String SEPARADOR = ":";
    private TreeMap<String, ArrayList<Curso>> plataforma;

    /**
     * Constructor  
     */
    public PlataformaCursos() {
        plataforma = new TreeMap<>();
    }

    /**
     * añadir un nuevo curso al map en la categoría indicada
     * Si ya existe la categoría se añade en ella el nuevo curso
     * (al final de la lista)
     * En caso contrario se creará una nueva entrada en el map con
     * la nueva categoría y el curso que hay en ella
     * Las claves siempre se añaden en mayúsculas  
     *  
     */
    public void addCurso(String categoria, Curso curso){
        categoria = categoria.toUpperCase();
        if(plataforma.containsKey(categoria)){
            ArrayList<Curso> cur = plataforma.get(categoria);
            cur.add(curso);
        }else{
            ArrayList<Curso> cur = new ArrayList<>();
            cur.add(curso);
            plataforma.put(categoria, cur);
        }
    }

    /**
     *  Devuelve la cantidad de cursos en la categoría indicada
     *  Si no existe la categoría devuelve -1
     *
     */
    public int totalCursosEn(String categoria) {
        categoria = categoria.toUpperCase();
        if(plataforma.containsKey(categoria)){
            return plataforma.get(categoria).size();
        }
        return -1;
    }

    /**
     * Representación textual de la plataforma (el map), cada categoría
     * junto con el nº total de cursos que hay en ella y a continuación
     * la relación de cursos en esa categoría (ver resultados de ejecución)
     * 
     * De forma eficiente ya que habrá muchas concatenaciones
     * 
     * Usar el conjunto de entradas y un iterador
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, ArrayList<Curso>>> entradas = plataforma.entrySet();
        Iterator<Map.Entry<String, ArrayList<Curso>>> it = entradas.iterator();
        sb.append("Cursos on line ofrecidos por la plataforma\n\n");
        while(it.hasNext()){
            Map.Entry<String, ArrayList<Curso>> aux = it.next();
            String clave = aux.getKey();
            sb.append(clave  + plataforma.get(clave).toString());
            sb.append("---------------------------------------\n");
        }
        return sb.toString();
    }

    /**
     * Mostrar la plataforma
     */
    public void escribir(){
        System.out.println(this.toString());
    }

    /**
     *  Lee de un fichero de texto la información de los cursos
     *  En cada línea del fichero se guarda la información de un curso
     *  con el formato "categoria:nombre:fecha publicacion:nivel"
     *  
     */
    public void leerDeFichero(){
        Scanner sc = new Scanner(
                this.getClass().getResourceAsStream("/cursos.csv"));
        while (sc.hasNextLine())  {
            String lineaCurso = sc.nextLine().trim();
            int p = lineaCurso.indexOf(SEPARADOR);
            String categoria = lineaCurso.substring(0, p).trim();
            Curso curso = obtenerCurso(lineaCurso.substring(p + 1));
            this.addCurso(categoria, curso);
        }
    }

    /**
     *  Dado un String con los datos de un curso
     *  obtiene y devuelve un objeto Curso
     *
     *  Ej. a partir de  "sql essential training: 3/12/2019 : principiante " 
     *  obtiene el objeto Curso correspondiente
     *  
     *  Asumimos todos los valores correctos aunque puede haber 
     *  espacios antes y después de cada dato
     */
    private Curso obtenerCurso(String lineaCurso){
        String[] str = lineaCurso.trim().split(SEPARADOR);
        LocalDate fecha = LocalDate.parse(str[1].trim(), 
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Curso curso = new Curso(str[0], fecha, 
                Nivel.valueOf(str[2].toUpperCase().trim()));
        return curso;
    }

    /**
     * devuelve un nuevo conjunto con los nombres de todas las categorías  
     *  
     */
    public TreeSet<String> obtenerCategorias(){
        TreeSet<String> categorias = new TreeSet<>();
        Set<String> conjuntoClaves = plataforma.keySet();
        for(String nombres: conjuntoClaves){
            categorias.add(nombres);
        }
        return categorias;
    }

    /**
     * borra de la plataforma los cursos de la categoría y nivel indicados
     * Se devuelve un conjunto (importa el orden) con los nombres de los cursos borrados 
     * 
     * Asumimos que existe la categoría
     *  
     */

    public TreeSet<String> borrarCursosDe(String categoria, Nivel nivel){
        categoria = categoria.toUpperCase();
        TreeSet<String> borrado = new TreeSet<>();
        ArrayList<Curso> cursosCategoria = plataforma.get(categoria);
        Iterator<Curso> it = cursosCategoria.iterator();
        while(it.hasNext()){
            Curso cur = it.next();
            if(nivel.equals(cur.getNivel())){
                borrado.add(cur.getNombre());
                it.remove();
            }
        }
        return borrado;
    }

    /**
     *   Devuelve el nombre del curso más antiguo en la
     *   plataforma (el primero publicado)
     */

    public String cursoMasAntiguo() {
        String cursoAntiguo = "";
        LocalDate fechaAntigua = LocalDate.MAX;
        Set<String> conjuntoCategorias = plataforma.keySet();
        for(String categoria: conjuntoCategorias){
            ArrayList<Curso> cursos = plataforma.get(categoria);
            for(Curso curso: cursos){
                if(curso.getFecha().compareTo(fechaAntigua) < 0){
                    fechaAntigua = curso.getFecha();
                    cursoAntiguo = curso.getNombre();
                }
            }
        }
        return cursoAntiguo;
    }

    /**
     *  
     */
    public static void main(String[] args){
        PlataformaCursos plataforma = new PlataformaCursos();
        plataforma.leerDeFichero();
        plataforma.escribir();

        System.out.println(
            "Curso más antiguo: " + plataforma.cursoMasAntiguo()
            + "\n");

        String categoria = "bases de datos";
        Nivel nivel = Nivel.AVANZADO;
        System.out.println("------------------");
        System.out.println(
            "Borrando cursos de " + categoria.toUpperCase()
            + " y nivel "
            + nivel);
        TreeSet<String> borrados = plataforma.borrarCursosDe(categoria, nivel);

        System.out.println("Borrados " + " = " + borrados.toString() + "\n");
        categoria = "cms";
        nivel = Nivel.INTERMEDIO;
        System.out.println(
            "Borrando cursos de " + categoria.toUpperCase()
            + " y nivel "
            + nivel);
        borrados = plataforma.borrarCursosDe(categoria, nivel);
        System.out.println("Borrados " + " = " + borrados.toString() + "\n");
        System.out.println("------------------\n");
        System.out.println(
            "Después de borrar ....");
        plataforma.escribir();
    }
}