import models.Bind;
import groovy.lang.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

public class Controller {
    private Object model;
    private List<String> yearsList;

    private static Map<String, Object> boundVariables;
    private static String latestDataText;

    public Controller(String modelName) {
        try {
            Class<?> cl = Class.forName("models." + modelName);
            model = cl.getConstructor().newInstance();

            if (boundVariables == null) {
                boundVariables = new LinkedHashMap<>();
            }
            if (latestDataText == null) {
                latestDataText = "";
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getYearsList() {
        return yearsList;
    }

    public Controller readDataFrom(String fName) {
        if (latestDataText.isEmpty()) {
            latestDataText = fName;
        } else if (!latestDataText.equals(fName)) {
            boundVariables = new LinkedHashMap<>();
            latestDataText = fName;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fName))) {
            String line;
            List<String> listOfStrings;
            int LL = 0;

            while ((line = bufferedReader.readLine()) != null) {
                listOfStrings = new ArrayList<>(Arrays.asList(line.split("\t")));
                if (listOfStrings.get(0).equals("LATA")) {
                    Field field = model.getClass().getDeclaredField("LL");
                    field.setAccessible(true);
                    field.set(model, LL = listOfStrings.size() - 1);
                    yearsList = listOfStrings;
                } else {
                    setModelFields(model, listOfStrings, LL);
                }
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public Controller runModel() {
        try {
            Method method = model.getClass().getMethod("run");
            method.invoke(model);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public Controller runScriptFromFile(String fName) {
        try {
            Binding binding = initiateBinding();
            GroovyShell groovyShell = new GroovyShell(binding);
            groovyShell.evaluate(new File(fName));
            updateBoundVariables(binding);
        } catch (IOException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public Controller runScript(String script) {
        try {
            Binding binding = initiateBinding();
            GroovyShell groovyShell = new GroovyShell(binding);
            groovyShell.evaluate(script);
            updateBoundVariables(binding);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public String getResultsAsTsv() {
        String result = "";
        try {
            Field[] fields = model.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(model).getClass().isArray()) {
                    double[] array = (double[]) field.get(model);
                    result += field.getName();
                    for (double value : array) {
                        result += "\t" + value;
                    }
                    result += "\n";
                }
            }

            for (Map.Entry<String, Object> entry : boundVariables.entrySet()) {
                result += entry.getKey();
                double[] array = (double[]) entry.getValue();
                for (double value : array) {
                    result += "\t" + value;
                }
                result += "\n";
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private void setModelFields(Object model, List<String> list, int arraySize)
    throws NoSuchFieldException, IllegalAccessException {
        Field field = model.getClass().getDeclaredField(list.get(0));
        field.setAccessible(true);
        list.remove(0);

        double[] arrayOfDoubles = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            double lastValue;
            if (list.size() <= i) {
                lastValue = Double.parseDouble(list.get(list.size() - 1));
            } else {
                lastValue = Double.parseDouble(list.get(i));
            }
            arrayOfDoubles[i] = lastValue;
        }
        field.set(model, arrayOfDoubles);
    }

    private Binding initiateBinding() throws IllegalAccessException {
        Binding binding = new Binding();
        Field[] fields = model.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bind.class)) {
                binding.setProperty(field.getName(), field.get(model));
            }
        }
        return binding;
    }

    private void updateBoundVariables(Binding binding) {
        Field[] fields = model.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bind.class)) {
                fieldNames.add(field.getName());
            }
        }

        Map<String, Object> vars = binding.getVariables();
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            if (!fieldNames.contains(entry.getKey()) && entry.getKey().length() != 1) {
                boundVariables.put(entry.getKey(), entry.getValue());
            }
        }
    }
}