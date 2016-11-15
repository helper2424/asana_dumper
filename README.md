# Dump all asana tasks to json

# If you have little project (less then 50 tasks)

Open dumper.js and setup in code project id. Copy dumper.js to browser console.

For example, https://app.asana.com/0/199352541227/list, 199352541227 is the project id

# If you have large project (more then 50 tasks)

1) Install Java to your computer. You can download installer here http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html ,
it's require java 1.8 or newer.

2) Open terminal and run this command
~~~~
java -version
~~~~

If you see something like this:

~~~~
java version "1.8.0_11"
Java(TM) SE Runtime Environment (build 1.8.0_11-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03, mixed mode)``
~~~~

It's mean that everything ok.

3) Next, install this extension for your browser https://chrome.google.com/webstore/detail/editthiscookie/fngmhnnpilhplaeedifhccceomclgfbg?hl=en.
 You can install any extension, we just need extract cookies from your browser.
4) After this, you should go to the asana site (now it's http://asana.com/ ) and open project, which you want dump.
5) Copy project id, I will call this <project_id>. http://take.ms/EbqVV , here <project_id> == 199352541227
6) Now we need authorization data for dumper. Open EditThisCookie browser extension and find cookie auth_token. Copy value of this cookie, this value I will call <auth_token>. Here example http://take.ms/JFKD6 , <auth_token> == 384f19b537a59d5c9d450d6921c0bba1
7) Repeat 4 step with ticket cookie, It's value I will call <ticket>. https://monosnap.com/file/p61pF53UqNgy058uISuGI4FWmxhdQA
8) Open terminal and go to the project folder. Go to the target directory.
```
cd <project_dir>/target
```
10) Start dumper like this
```
java -jar asana_dumper.jar <project_id> <auth_token> <ticket>
```
With my values it will look like this
 ```
 java -jar asana_dumper.jar 199352541227 384f19b537a59d5c9d450d6921c0bba1 7f1eaffd985ccf46e792b879b6187e3c658c06c641158c884acc23405af3f3d3
```

11) When script finish all your dumps will be in <project_dir>/target/out directory in json format
