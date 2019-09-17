package ray.surface;

import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
	
	/** The center of the sphere. */
	public final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the sphere. */
	public double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	public Sphere() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "sphere " + center + " " + radius + " " + shader + " end";
	}
	
}
