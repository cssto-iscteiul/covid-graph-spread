# covid-graph-spread

82714 Catarina Teodoro

covid-graph-spread is a small java application that extracts covid19spreading.rdf 
files associated to tags in available in  

covid-graph-spread is a small java application that extracts covid19spreading.rdf files associated to tags available in http://github.com/vbasto-iscte/ESII1920 repository and presents information about them in a HTML table to be shown in a WP-CMS web site.

Follow these steps to test this app:

1. create a folder on your computer named wordpress;
2. move the files Dockerfile and docker-compose.yml (provided in this project) to that folder;
3. on your cmd move to the folder you have created;
4. write the command "docker build -t wordpress-with-java:5.4.1 .";
5. once the previous command is finished write the command "docker-compose up -d"
6. on your wordpress folder you will find a folder named cgi-bin, paste the .jar and .sh files provided in this project on that folder
7. once you finished step 4 and 5 on your browser go to http://localhost/wp-admin/install.php and install wordpress and create your user
8. edit one of the posts on the website and add this simple form:
"form method="POST" action="cgi-bin/covid-graph-spread.sh""
"input type="submit" value="covid-graph-spread""
"/form"
9. once you're done, on your http://localhost page you should see a button to run the app and see the result!
