package ru.hgstudio;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.CookieStore;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Handler implements Runnable {
    private final int min;
    private final int max;
    private final ArrayList<Long> tasks;
    private final ArrayList<String> tasksNames;
    private final CookieStore cookies;

    Handler(ArrayList<Long> tasks, ArrayList<String> tasksNames, int min, int max, CookieStore cookies) {
        this.tasksNames = tasksNames;
        this.tasks = tasks;
        this.min = min;
        this.max = max;
        this.cookies = cookies;
    }

    @Override
    public void run() {
        try {
            int i = this.min;
            while (i <= this.max && i < this.tasks.size()) {
                boolean ok = true;
                JsonParser parser = new JsonParser();
                JsonObject stories = null;
                JsonObject taskData = null;
                String taskId = this.tasks.get(i).toString();

                try {
                    taskData = Main.requestData(this.cookies, "https://app.asana.com/api/1.0/tasks/" + taskId);
                } catch (Exception error) {
                    System.out.println(String.format("Error %s", error.getMessage()));
                    ok = false;
                }

                try {
                    stories = Main.requestData(this.cookies, "https://app.asana.com/api/1.0/tasks/" + taskId + "/stories");
                } catch (Exception error) {
                    System.out.println(String.format("Error %s", error.getMessage()));
                    ok = false;
                }

                if (ok) {
                    System.out.println("Finished task " + String.valueOf(i) + " " + taskId);
                    stories.addProperty("id", taskId);
                    stories.addProperty("name", this.tasksNames.get(i));
                    stories.addProperty("description", taskData.getAsJsonObject("data").get("notes").getAsString());

                    Files.write(Paths.get("./out/" + this.tasks.get(i).toString() + ".json"),
                            new GsonBuilder().setPrettyPrinting().create().toJson(stories).getBytes("UTF8"));

                    Thread.sleep(500 + (int) (Math.random() * 100));
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println(String.format("Error %s", e.getMessage()));
        }
    }
}