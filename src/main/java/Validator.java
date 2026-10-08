import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Validator {

    private static final Map<String, String> NAMES = Map.ofEntries(
            Map.entry("number", "Номер зачетной книжки"),
            Map.entry("surname", "Фамилия"),
            Map.entry("name", "Имя"),
            Map.entry("patronymic", "Отчество"),
            Map.entry("group", "Группа"),
            Map.entry("faculty", "Факультет"),
            Map.entry("sessions", "Сессии"),
            Map.entry("sessions.number", "Номер сессии"),
            Map.entry("course", "Курс"),
            Map.entry("averageMark", "Средний балл"),
            Map.entry("exams", "Экзамены"),
            Map.entry("credits", "Зачеты"),
            Map.entry("subject", "Предмет"),
            Map.entry("mark", "Оценка"),
            Map.entry("passed", "Зачтено")
    );

    public static void main(String[] args) {
        check("gradebook.json", "schema.json");
        check("gradebook_bad.json", "schema.json");
    }

    public static void check(String dataFile, String schemaFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode data = mapper.readTree(new File(dataFile));

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            JsonSchema schema;
            try (InputStream in = Files.newInputStream(Path.of(schemaFile))) {
                schema = factory.getSchema(in);
            }

            Set<ValidationMessage> errors = schema.validate(data);
            if (errors.isEmpty()) {
                System.out.println("Файл соответствует схеме");
                System.out.println();
                printNode(data, "", "");
            } else {
                System.out.println("Файл не соответствует схеме, нарушений: " + errors.size());
                for (ValidationMessage m : errors) {
                    System.out.println("  " + m.getMessage());
                }
            }
        } catch (IOException e) {
        System.out.println("Ошибка чтения: " + e);
    }
        System.out.println();
    }

    private static void printNode(JsonNode node, String parentKey, String indent) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String label = label(parentKey, field.getKey());
                JsonNode value = field.getValue();
                if (value.isValueNode()) {
                    System.out.println(indent + label + ": " + text(value));
                } else {
                    System.out.println(indent + label + ":");
                    printNode(value, field.getKey(), indent + "    ");
                }
            }
        } else if (node.isArray()) {
            int i = 1;
            for (JsonNode element : node) {
                System.out.println(indent + "[" + i + "]");
                printNode(element, parentKey, indent + "    ");
                i++;
            }
        }
    }

    private static String label(String parentKey, String key) {
        String full = parentKey + "." + key;
        if (NAMES.containsKey(full)) {
            return NAMES.get(full);
        }
        return NAMES.getOrDefault(key, key);
    }

    private static String text(JsonNode value) {
        if (value.isBoolean()) {
            return value.asBoolean() ? "да" : "нет";
        }
        return value.asText();
    }
}