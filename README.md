# HYU 2015-1 "Computer Graphics (CSE4020)" Ray Tracing Assignment

This repository contains source code and scene data with xml format, and generated scene images.  
In this assignment, skeleton code was given.
- In ```ray``` directory, there is ```RayTracer.java```. This is skeleton code.
- Other source files are acting as a library and not a skeleton code.


### Before start

You must compile first. Run below command.
```sh
$ javac ray/RayTracer.java
```

After above command has been executed, we will get java class files.  
Now we can generate scene image.


### How to generate scene images

Just run below code.
```sh
$ java ray.RayTracer [Scene Data with XML Format]
```

In this project, we can find scenes directory and sample xml files.  
Below command will generate ```four-spheres.xml.png``` in the ```scene``` directory.

```sh
$ java ray.RayTracer scenes/four-spheres.xml
```
![four-spheres](https://github.com/khhan1993/2015-HYU-CSE-Computer-Graphics-Ray-Tracing-Assignment/raw/master/scenes/four-spheres.xml.png)
