package ru.hgstudio;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        int cores = 8;

        System.out.println("Asana dumper started");

        CookieStore cookieStore = new BasicCookieStore();

        BasicClientCookie cookie = new BasicClientCookie("auth_token", args[1]);
        cookie.setDomain(".app.asana.com");
        cookie.setPath("/");
        cookie.setSecure(true);
        cookieStore.addCookie(cookie);

        BasicClientCookie cookie2 = new BasicClientCookie("ticket", args[2]);
        cookie2.setDomain(".app.asana.com");
        cookie2.setPath("/");
        cookie2.setSecure(true);
        cookieStore.addCookie(cookie2);

        System.out.println("Create cookies" + cookieStore.toString());
        System.out.println("Load tasks");

        ArrayList<Long> tasks = null;
        ArrayList<String> taskNames = null;

        try {

            JsonObject contentBody = requestData(cookieStore, "https://app.asana.com/api/1.0/projects/" + args[0] + "/tasks");
            JsonArray data = contentBody.getAsJsonArray("data");

            tasks = new ArrayList<Long>(data.size());
            taskNames = new ArrayList<String>(data.size());

            System.out.println("Loaded " + String.valueOf(data.size()) + " tasks");

            Iterator i = data.iterator();
            while (i.hasNext()) {
                JsonObject val = (JsonObject) i.next();
                tasks.add(val.get("id").getAsLong());
                taskNames.add(val.get("name").getAsString());
            }
        } catch (Exception e) {
            System.out.println(String.format("Error %s", e.getMessage()));
        }

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("Tasks list is empty");
            return;
        }

        try {
            Files.createDirectories(Paths.get("./out"));
        }
        catch(IOException error) {
            System.out.println("Can't create ./out directory");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(cores);

        int step = (int)Math.ceil((float) tasks.size() / cores);
        for (int i = 0; i < cores; i++) {
            executor.execute(new Handler(tasks, taskNames, i * step, (i + 1) * step - 1, cookieStore));
        }

        executor.shutdown();

        while(!executor.isTerminated()) {

        }

        System.out.println("Finished dump asana tasks");
    }

    public static JsonObject requestData(CookieStore cookies, String uri) throws IOException {
        Executor executor = Executor.newInstance();
        String content = executor.use(cookies).execute(createRequest(uri)).returnContent().asString();

        JsonParser parser = new JsonParser();
        return parser.parse(content).getAsJsonObject();
    }

    public static Request createRequest(String uri) {
        Request res = Request.Get(uri);
        res.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
        res.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
        return res;
    }
}
