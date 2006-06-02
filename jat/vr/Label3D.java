/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 The JAT Project and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package jat.vr;

import java.awt.*;
import java.awt.image.*;
import javax.media.j3d.*;
import javax.vecmath.*;


/** Label3D class
 * @author Tobias Berthold
 */
public class Label3D extends Body3D
{
	float size;

	/**
	 * Method Label3D.
	 * @param szText
	 * @param x
	 * @param y
	 * @param z
	 */
	//	public Label3D(float size)
	//	{
	//		super(myapplet);
	//		this.size = size;
	//		addChild(new ColorCube(size));
	//	}

	public Label3D(String szText, float x, float y, float z)
	{

		BufferedImage bufferedImage = new BufferedImage(50, 20, BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.getGraphics();
		g.setColor(Color.white);
		g.drawString(szText, 10, 10);

		ImageComponent2D imageComponent2D = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bufferedImage);
		imageComponent2D.setCapability(ImageComponent.ALLOW_IMAGE_READ);
		imageComponent2D.setCapability(ImageComponent.ALLOW_SIZE_READ);

		// create the Raster for the image
		javax.media.j3d.Raster renderRaster =
			new javax.media.j3d.Raster(
				new Point3f(x, y, z),
				javax.media.j3d.Raster.RASTER_COLOR,
				0,
				0,
				bufferedImage.getWidth(),
				bufferedImage.getHeight(),
				imageComponent2D,
				null);

		//		return new Shape3D(renderRaster);

		addChild(new Shape3D(renderRaster));
		set_position(x, y, z);
	}
}