package ray;

import java.util.ArrayList;
import java.util.Iterator;

import ray.shader.Shader;
import ray.surface.Surface;


/**
 * The scene is just a collection of objects that compose a scene. The camera,
 * the list of lights, and the list of surfaces.
 *
 * @author ags
 */
public class Scene {
	
	/** The camera for this scene. */
	protected Camera camera;
	public void setCamera(Camera camera) { this.camera = camera; }
	public Camera getCamera() { return this.camera; }
	
	/** The list of lights for the scene. */
	protected ArrayList lights = new ArrayList();
	public void addLight(Light toAdd) { lights.add(toAdd); }
	public ArrayList<Light> getLights() { return this.lights; }
	
	/** The list of surfaces for the scene. */
	protected ArrayList surfaces = new ArrayList();
	public void addSurface(Surface toAdd) { surfaces.add(toAdd); }
	public ArrayList<Surface> getSurfaces() { return this.surfaces; }
	
	/** The list of materials in the scene . */
	protected ArrayList shaders = new ArrayList();
	public void addShader(Shader toAdd) { shaders.add(toAdd); }
	public ArrayList<Shader> getShaders() { return this.shaders; }
	
	/** Image to be produced by the renderer **/
	protected Image outputImage;
	public Image getImage() { return this.outputImage; }
	public void setImage(Image outputImage) { this.outputImage = outputImage; }

}
