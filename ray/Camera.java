package ray;

import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a simple camera.
 */
public class Camera {
	
	/*
	 * Fields that are read in from the input file to describe the camera.
	 * You'll probably want to store some derived values to make ray generation easy.
	 */
	
	protected final Point3 viewPoint = new Point3();
	public void setViewPoint(Point3 viewPoint) { this.viewPoint.set(viewPoint); }
	
	protected final Vector3 viewDir = new Vector3(0, 0, -1);
	public void setViewDir(Vector3 viewDir) { this.viewDir.set(viewDir); }
	
	protected final Vector3 viewUp = new Vector3(0, 1, 0);
	public void setViewUp(Vector3 viewUp) { this.viewUp.set(viewUp); }
	
	protected final Vector3 projNormal = new Vector3(0, 0, 1);
	public void setProjNormal(Vector3 projNormal) { this.projNormal.set(projNormal); }
	
	protected double viewWidth = 1.0;
	public void setViewWidth(double viewWidth) { this.viewWidth = viewWidth; }
	
	protected double viewHeight = 1.0;
	public void setViewHeight(double viewHeight) { this.viewHeight = viewHeight; }
	
	protected double projDistance = 1.0;
	public void setprojDistance(double projDistance) { this.projDistance = projDistance; }
	
}
