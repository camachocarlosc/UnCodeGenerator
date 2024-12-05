import java.io.*;
import java.util.*;

public class PlantUMLToJava {

    public static void main(String[] args) {
        String inputFilePath = "diagram.puml"; 
        String outputDirectory = "output/";   

        try {           
            List<String> lines = readFile(inputFilePath);

            List<JavaClass> classes = parsePlantUML(lines);

            generateJavaCode(classes, outputDirectory);

            System.out.println("Clases generadas exitosamente en el directorio: " + outputDirectory);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        }
        return lines;
    }

    private static List<JavaClass> parsePlantUML(List<String> lines) {
        List<JavaClass> classes = new ArrayList<>();
        JavaClass currentClass = null;

        for (String line : lines) {
            if (line.startsWith("class ")) {
                String className = line.split(" ")[1];
                currentClass = new JavaClass(className);
                classes.add(currentClass);
            } else if (line.startsWith("}")) {
                currentClass = null;
            } else if (currentClass != null) {
                if (line.contains(":")) {
                    if (line.contains("(") && line.contains(")")) {
                        currentClass.addMethod(line);
                    } else {
                        currentClass.addAttribute(line);
                    }
                }
            } else if (line.contains(" --|> ")) {
                String[] parts = line.split(" --\\|> ");
                String child = parts[0].trim();
                String parent = parts[1].trim();
                findClassByName(classes, child).setParent(parent);
            } else if (line.contains(" --> ") || line.contains(" <-- ")) {
                String[] parts = line.split(" --[<>]+ ");
                String source = parts[0].trim();
                String target = parts[1].trim();
                findClassByName(classes, source).addRelationship(target);
            }
        }
        return classes;
    }

    private static JavaClass findClassByName(List<JavaClass> classes, String name) {
        return classes.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private static void generateJavaCode(List<JavaClass> classes, String outputDirectory) throws IOException {
        File dir = new File(outputDirectory);
        if (!dir.exists()) dir.mkdirs();

        for (JavaClass javaClass : classes) {
            String filePath = outputDirectory + javaClass.getName() + ".java";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(javaClass.generateCode());
            }
        }
    }
}

class JavaClass {
    private String name;
    private String parent;
    private List<String> attributes;
    private List<String> methods;
    private List<String> relationships;

    public JavaClass(String name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public void addMethod(String method) {
        methods.add(method);
    }

    public void addRelationship(String relatedClass) {
        relationships.add(relatedClass);
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();
        code.append("public class ").append(name);
        if (parent != null) {
            code.append(" extends ").append(parent);
        }
        code.append(" {\n");

        for (String attr : attributes) {
            String[] parts = attr.split(" : ");
            String modifier = parts[0].startsWith("+") ? "public" : "private";
            String type = parts[1];
            String attrName = parts[0].substring(1).trim();
            code.append("    ").append(modifier).append(" ").append(type).append(" ").append(attrName).append(";\n");
        }

        for (String rel : relationships) {
            code.append("    private ").append(rel).append(" ").append(rel.toLowerCase()).append(";\n");
        }

        for (String method : methods) {
            String[] parts = method.split(" : ");
            String modifier = parts[0].startsWith("+") ? "public" : "private";
            String returnType = parts[1];
            String signature = parts[0].substring(1).trim();

            code.append("    ").append(modifier).append(" ").append(returnType).append(" ").append(signature).append(" {\n");
            if (!returnType.equals("void")) {
                String returnName = getReturnName(signature);
                code.append("        return ").append(returnName).append(";\n");
            }
            code.append("    }\n");
        }

        code.append("}\n");
        return code.toString();
    }

    private String getReturnName(String signature) {
        String attrName = signature.contains("(") ? signature.split("\\(")[0] : signature;
        return attrName.toLowerCase();
    }
}