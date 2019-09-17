package ray.shader;

import ray.math.Color;
import ray.math.Vector3;

/**
 * This interface specifies what is necessary for an object to be a material.
 * You will probably want to add a "shade" method to it.
 * @author ags
 */
public interface Shader {

  /**
   * The material given to all surfaces unless another is specified.
   */
  public static final Shader DEFAULT_MATERIAL = new Lambertian();

}
