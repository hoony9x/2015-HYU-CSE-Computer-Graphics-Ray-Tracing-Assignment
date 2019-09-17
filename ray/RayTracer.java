package ray;

import java.util.ArrayList;
import ray.math.Color;
import ray.math.Vector3;
import ray.math.Point3;
import ray.surface.*;
import ray.shader.*;

/**
 * A simple ray tracer.
 *
 * @author ags
 */
public class RayTracer {

    /**
     * The main method takes all the parameters an assumes they are input files
     * for the ray tracer. It tries to render each one and write it out to a PNG
     * file named <input_file>.png.
     *
     * @param args
     */
    public static final void main(String[] args) {

        Parser parser = new Parser();
        for (int ctr = 0; ctr < args.length; ctr++) {

            // Get the input/output filenames.
            String inputFilename = args[ctr];
            String outputFilename = inputFilename + ".png";

            // Parse the input file
            Scene scene = (Scene) parser.parse(inputFilename, Scene.class);

            // Render the scene
            renderImage(scene);

            // Write the image out
            scene.getImage().write(outputFilename);
        }
    }

    private static void printVec3(Vector3 v) {
        System.out.print("("+String.valueOf(v.x)+", "+
                String.valueOf(v.y)+", "+
                String.valueOf(v.z)+") ");
    }
    private static void printPos3(Point3 v) {
        System.out.print("("+String.valueOf(v.x)+", "+
                String.valueOf(v.y)+", "+
                String.valueOf(v.z)+") ");
    }

    /**
     * The renderImage method renders the entire scene.
     *
     * @param scene The scene to be rendered
     */
    public static void renderImage(Scene scene) {

        // Get the output image
        Image image = scene.getImage();

        // Timing counters
        long startTime = System.currentTimeMillis();

        /*
         * Render the image, writing the pixel values into image.
         */

        /* 카메라 초기 정보 로드, 새 객체로 생성 */
        Point3 viewPoint = scene.getCamera().viewPoint;
        viewPoint = new Point3(viewPoint.x, viewPoint.y, viewPoint.z);

        Vector3 viewDir = scene.getCamera().viewDir;
        viewDir = new Vector3(viewDir.x, viewDir.y, viewDir.z);
        viewDir.normalize();

        Vector3 viewUp = scene.getCamera().viewUp;
        viewUp = new Vector3(viewUp.x, viewUp.y, viewUp.z);
        viewUp.normalize();

        Vector3 projNormal = scene.getCamera().projNormal;
        projNormal = new Vector3(projNormal.x, projNormal.y, projNormal.z);
        projNormal.normalize();

        double projDistance = scene.getCamera().projDistance;


        /* Image plane의 중앙 점 지정 */
        Point3 planeCenterPoint = new Point3(viewPoint.x, viewPoint.y, viewPoint.z);
        planeCenterPoint.scaleAdd(projDistance, viewDir);


        /* Image plane을 표현하기 위한 벡터 U와 V를 지정 */
        /* plane = planeCenterPoint + A*planeU + B*planeV */
        Vector3 planeV = new Vector3();
        planeV.cross(viewUp, projNormal);
        planeV.normalize();
        Vector3 planeU = new Vector3();
        planeU.cross(projNormal, planeV);
        planeV.normalize();


        /* Image Size 로드 */
        double width = scene.getCamera().viewWidth;
        double height = scene.getCamera().viewHeight;

        int pixelWidth = image.getWidth();
        int pixelHeight = image.getHeight();

        int halfWidth = pixelWidth / 2;
        int halfHeight = pixelHeight / 2;


        /* Camera Ray를 생성, 이를 이용하여 Tracing */
        for(int i = -halfWidth; i <= halfWidth - 1; i++)
        {
            for(int j = -halfHeight; j <= halfHeight - 1; j++)
            {
                Point3 targetPoint = new Point3(planeCenterPoint);
                targetPoint.scaleAdd((double)i / (double)halfWidth * width, planeV);
                targetPoint.scaleAdd((double)j / (double)halfHeight * height, planeU);

                Vector3 targetVector = new Vector3();
                targetVector.sub(targetPoint, viewPoint);
                targetVector.normalize();

                Color targetPointColor = calculateColor(scene, viewPoint, targetVector);
                image.setPixelColor(targetPointColor, i + halfWidth, j + halfHeight);
            }
        }


        // Output time
        long totalTime = (System.currentTimeMillis() - startTime);
        System.out.println("Done.  Total rendering time: " + (totalTime / 1000.0) + " seconds");
    }

    private static Color calculateColor(Scene scene, Point3 viewPoint, Vector3 direction)
    {
        ArrayList surfaces = scene.getSurfaces();
        double projDistance = scene.getCamera().projDistance;

        Surface targetSurface = (Surface)surfaces.get(0);
        double targetT = 2147483647.0;
        boolean isexist = false;

        for(int i = 0; i < surfaces.size(); i++)
        {
            if(surfaces.get(i) instanceof ray.surface.Sphere) //Sphere
            {
                Point3 sphereCenter = new Point3(((Sphere)surfaces.get(i)).center);
                double sphereRadius = ((Sphere)surfaces.get(i)).radius;

                Vector3 u = new Vector3();
                u.sub(viewPoint, sphereCenter);

                double t = -u.dot(direction) - Math.sqrt(Math.pow(u.dot(direction), 2.0) - u.dot(u) + Math.pow(sphereRadius, 2.0));
                if(t < 0)
                    t = -u.dot(direction) + Math.sqrt(Math.pow(u.dot(direction), 2.0) - u.dot(u) + Math.pow(sphereRadius, 2.0));
                
                if(targetT > t && t > 0)
                {
                    targetT = t;
                    targetSurface = (Surface)surfaces.get(i);
                    isexist = true;
                }
            }
            else //Box
            {
                Point3 minPt = new Point3(((Box)surfaces.get(i)).minPt);
                Point3 maxPt = new Point3(((Box)surfaces.get(i)).maxPt);

                double tx1 = (minPt.x - viewPoint.x) / direction.x;
                double ty1 = (minPt.y - viewPoint.y) / direction.y;
                double tz1 = (minPt.z - viewPoint.z) / direction.z;

                double tx2 = (maxPt.x - viewPoint.x) / direction.x;
                double ty2 = (maxPt.y - viewPoint.y) / direction.y;
                double tz2 = (maxPt.z - viewPoint.z) / direction.z;

                double txmin = Math.min(tx1, tx2);
                double tymin = Math.min(ty1, ty2);
                double tzmin = Math.min(tz1, tz2);

                double txmax = Math.max(tx1, tx2);
                double tymax = Math.max(ty1, ty2);
                double tzmax = Math.max(tz1, tz2);

                double tmin = Math.max(txmin, tymin);
                tmin = Math.max(tmin, tzmin);

                double tmax = Math.min(txmax, tymax);
                tmax = Math.min(tmax, tzmax);

                if(tmin > tmax)
                    continue;

                double t = tmin;

                if(targetT > t && t > 0)
                {
                    targetT = t;
                    targetSurface = (Surface)surfaces.get(i);
                    isexist = true;
                }
            }
        }

        if(isexist == false)
            return (new Color(0, 0, 0));

        Point3 tgPoint = new Point3(viewPoint);
        tgPoint.scaleAdd(targetT, direction);

        Shader shader = targetSurface.shader;
        ArrayList lightlist = scene.getLights();

        Color returnColor = new Color();

        if(targetSurface instanceof ray.surface.Sphere) //Sphere
        {
            Sphere sphere = (Sphere)targetSurface;
            Point3 sphereCenter = new Point3(sphere.center);
            double sphereRadius = sphere.radius;

            Vector3 nVec = new Vector3();
            nVec.sub(tgPoint, sphereCenter);
            nVec.normalize();

            if (shader.getClass().equals(Lambertian.class)) //Lambertian
            {
                Lambertian lam = (Lambertian)shader;

                for(int i = 0; i < lightlist.size(); i++)
                {
                    Light light = (Light)lightlist.get(i);
                    Point3 lightPos = new Point3(light.position);
                    Color lightIntense = new Color(light.intensity);

                    Vector3 lightDir = new Vector3();
                    lightDir.sub(lightPos, tgPoint);
                    lightDir.normalize();

                    Color diffuse = new Color();

                    diffuse.add(lam.diffuseColor);
                    diffuse.scale(lightIntense);
                    if(nVec.dot(lightDir) > 0)
                        diffuse.scale(nVec.dot(lightDir));
                    else
                        diffuse.set(0, 0, 0);

                    if(CheckIfShadow(surfaces, tgPoint, lightPos) < 2)
                    {
                        returnColor.add(diffuse);
                    }
                }
            }
            else //Phong
            {
                Phong pho = (Phong)shader;

                for(int i = 0; i < lightlist.size(); i++)
                {
                    Light light = (Light)lightlist.get(i);
                    Point3 lightPos = new Point3(light.position);
                    Color lightIntense = new Color(light.intensity);

                    Vector3 lightDir = new Vector3();
                    lightDir.sub(lightPos, tgPoint);
                    lightDir.normalize();

                    Color diffuse = new Color();

                    diffuse.add(pho.diffuseColor);
                    diffuse.scale(lightIntense);
                    if(nVec.dot(lightDir) > 0)
                        diffuse.scale(nVec.dot(lightDir));
                    else
                        diffuse.set(0, 0, 0);


                    Vector3 hVec = new Vector3();
                    hVec.add(new Vector3(-direction.x, -direction.y, -direction.z), lightDir);
                    hVec.normalize();

                    Color specular = new Color();
                    double exponent = pho.exponent;

                    specular.add(pho.specularColor);
                    specular.scale(lightIntense);
                    if(nVec.dot(hVec) > 0)
                        specular.scale(Math.pow(nVec.dot(hVec), exponent));
                    else
                        specular.set(0, 0, 0);

                    if(CheckIfShadow(surfaces, tgPoint, lightPos) < 2)
                    {
                        returnColor.add(diffuse);
                        returnColor.add(specular);
                    }
                }
            }
        }
        else if(targetSurface instanceof ray.surface.Box) //Box
        {
            Box box = (Box)targetSurface;
            Point3 minPt = box.minPt;
            Point3 maxPt = box.maxPt;
            Point3 midPt = new Point3((minPt.x + maxPt.x) / 2.0, (minPt.y + maxPt.y) / 2.0, (minPt.z + maxPt.z) / 2.0);

            double XY = (maxPt.x - minPt.x) * (maxPt.y - minPt.y);
            double YZ = (maxPt.y - minPt.y) * (maxPt.z - minPt.z);
            double ZX = (maxPt.z - minPt.z) * (maxPt.x - minPt.x);

            Vector3 tempVec = new Vector3();
            tempVec.sub(tgPoint, midPt);
            tempVec.normalize();

            Vector3 nVec = new Vector3(1, 0, 0);
            double chk = tempVec.dot(nVec) * YZ;

            if(tempVec.dot(new Vector3(0, 1, 0)) * ZX > chk)
            {
                nVec.set(0, 1, 0);
                chk = tempVec.dot(nVec) * ZX;
            }

            if(tempVec.dot(new Vector3(0, 0, 1)) * XY > chk)
            {
                nVec.set(0, 0, 1);
                chk = tempVec.dot(nVec) * XY;
            }

            if(tempVec.dot(new Vector3(-1, 0, 0)) * YZ > chk)
            {
                nVec.set(-1, 0, 0);
                chk = tempVec.dot(nVec) * YZ;
            }

            if(tempVec.dot(new Vector3(0, -1, 0)) * ZX > chk)
            {
                nVec.set(0, -1, 0);
                chk = tempVec.dot(nVec) * ZX;
            }

            if(tempVec.dot(new Vector3(0, 0, -1)) * XY > chk)
            {
                nVec.set(0, 0, -1);
                chk = tempVec.dot(nVec) * XY;
            }
            
            nVec.normalize();

            if (shader.getClass().equals(Lambertian.class)) //Lambertian
            {
                Lambertian lam = (Lambertian)shader;

                for(int i = 0; i < lightlist.size(); i++)
                {
                    Light light = (Light)lightlist.get(i);
                    Point3 lightPos = new Point3(light.position);
                    Color lightIntense = new Color(light.intensity);

                    Vector3 lightDir = new Vector3();
                    lightDir.sub(lightPos, tgPoint);
                    lightDir.normalize();

                    Color diffuse = new Color();

                    diffuse.add(lam.diffuseColor);
                    diffuse.scale(lightIntense);
                    if(nVec.dot(lightDir) > 0)
                        diffuse.scale(nVec.dot(lightDir));
                    else
                        diffuse.set(0, 0, 0);

                    if(CheckIfShadow(surfaces, tgPoint, lightPos) == 0)
                    {
                        returnColor.add(diffuse);
                    }
                }

            }
            else //Phong
            {
                Phong pho = (Phong)shader;

                for(int i = 0; i < lightlist.size(); i++)
                {
                    Light light = (Light)lightlist.get(i);
                    Point3 lightPos = new Point3(light.position);
                    Color lightIntense = new Color(light.intensity);

                    Vector3 lightDir = new Vector3();
                    lightDir.sub(lightPos, tgPoint);
                    lightDir.normalize();

                    Color diffuse = new Color();

                    diffuse.add(pho.diffuseColor);
                    diffuse.scale(lightIntense);
                    if(nVec.dot(lightDir) > 0)
                        diffuse.scale(nVec.dot(lightDir));
                    else
                        diffuse.set(0, 0, 0);

                    Vector3 hVec = new Vector3();
                    hVec.add(new Vector3(-direction.x, -direction.y, -direction.z), lightDir);
                    hVec.normalize();

                    Color specular = new Color();
                    double exponent = pho.exponent;

                    specular.add(pho.specularColor);
                    specular.scale(lightIntense);
                    if(nVec.dot(hVec) > 0)
                        specular.scale(Math.pow(nVec.dot(hVec), exponent));
                    else
                        specular.set(0, 0, 0);

                    if(CheckIfShadow(surfaces, tgPoint, lightPos) == 0)
                    {
                        returnColor.add(diffuse);
                        returnColor.add(specular);
                    }
                }
            }
        }

        return returnColor;
    }

    private static int CheckIfShadow(ArrayList surfaces, Point3 tgPoint, Point3 lightPos)
    {
        int count = 0;

        Vector3 direction = new Vector3();
        direction.sub(lightPos, tgPoint);
        direction.normalize();

        for(int i = 0; i < surfaces.size(); i++)
        {
            if(surfaces.get(i) instanceof ray.surface.Sphere) //Sphere
            {
                Point3 sphereCenter = new Point3(((Sphere)surfaces.get(i)).center);
                double sphereRadius = ((Sphere)surfaces.get(i)).radius;

                Vector3 u = new Vector3();
                u.sub(tgPoint, sphereCenter);

                double t1 = -u.dot(direction) - Math.sqrt(Math.pow(u.dot(direction), 2.0) - u.dot(u) + Math.pow(sphereRadius, 2.0));
                double t2 = -u.dot(direction) + Math.sqrt(Math.pow(u.dot(direction), 2.0) - u.dot(u) + Math.pow(sphereRadius, 2.0));

                if(t1 > 0)
                    count++;

                if(t2 > 0)
                    count++;
            }
            else //Box
            {
                Point3 minPt = new Point3(((Box)surfaces.get(i)).minPt);
                Point3 maxPt = new Point3(((Box)surfaces.get(i)).maxPt);

                double tx1 = (minPt.x - tgPoint.x) / direction.x;
                double ty1 = (minPt.y - tgPoint.y) / direction.y;
                double tz1 = (minPt.z - tgPoint.z) / direction.z;

                double tx2 = (maxPt.x - tgPoint.x) / direction.x;
                double ty2 = (maxPt.y - tgPoint.y) / direction.y;
                double tz2 = (maxPt.z - tgPoint.z) / direction.z;

                double txmin = Math.min(tx1, tx2);
                double tymin = Math.min(ty1, ty2);
                double tzmin = Math.min(tz1, tz2);

                double txmax = Math.max(tx1, tx2);
                double tymax = Math.max(ty1, ty2);
                double tzmax = Math.max(tz1, tz2);

                double tmin = Math.max(txmin, tymin);
                tmin = Math.max(tmin, tzmin);

                double tmax = Math.min(txmax, tymax);
                tmax = Math.min(tmax, tzmax);

                if(tmin > tmax)
                    continue;

                double t = tmin;

                if(t > 0)
                    count++;
            }
        }

        return count;
    }
}
