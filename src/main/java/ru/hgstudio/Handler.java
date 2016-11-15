package ru.hgstudio;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.client.CookieStore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.hgstudio.Main;

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
                JSONObject stories = null;
                try {
                    stories = Main.requestData(this.cookies, "https://app.asana.com/api/1.0/tasks/" + this.tasks.get(i).toString() + "/stories");
                }
                catch(Exception error) {
                    System.out.println(String.format("Error %s", error.getMessage()));
                    ok = false;
                }

                if (ok) {
                    System.out.println("Finished task " +  String.valueOf(i) + " " + this.tasks.get(i).toString());
                    stories.put("id", this.tasks.get(i));
                    stories.put("name", this.tasksNames.get(i));
                    Files.write(Paths.get("./out/" + this.tasks.get(i).toString() + ".json"), stories.toJSONString().getBytes());

                    Thread.sleep(500 + (int)(Math.random() * 100));
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println(String.format("Error %s", e.getMessage()));
        }
    }
}