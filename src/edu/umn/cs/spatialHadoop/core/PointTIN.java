package edu.umn.cs.spatialHadoop.core;

import java.awt.Graphics;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.io.Text;

import edu.umn.cs.spatialHadoop.io.TextSerializerHelper;

/**
 * A class that holds coordinates of a point.
 * @author aseldawy
 *
 */
public class PointTIN implements Shape {
	public String quadIndex = "";
	public String pointIDStr = "";
	public double x = 0;
	public double y = 0;
	public Set<String> triangleList = new HashSet<>();

	public PointTIN() {
		this("", "" , 0, 0);
	}
	
	public PointTIN(String quadIndex, String pointID, double x, double y) {
	  set(quadIndex, pointID, x, y);
	}
	

	public void set(String quadIndex, String pointID,double x, double y) {
		this.quadIndex = quadIndex;
		this.pointIDStr = pointID;
		this.x = x;
		this.y = y;
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(quadIndex);
		out.writeUTF(pointIDStr);
		out.writeDouble(x);
		out.writeDouble(y);
		
		Iterator<String> iter = triangleList.iterator();
		while(iter.hasNext())
		{
			out.writeUTF(iter.next());
		}
	}

	public void readFields(DataInput in) throws IOException {
		this.quadIndex = in.readUTF();
		this.pointIDStr = in.readUTF();
		this.x = in.readDouble();
		this.y = in.readDouble();
		
		
		triangleList.clear();
		String adjTri = "";
		adjTri = in.readUTF();
		while(adjTri != null && adjTri != "\0")
		{
			triangleList.add(adjTri);
			adjTri = in.readUTF();
		}
	}

	public int compareTo(Shape s) {
		PointTIN pt2 = (PointTIN) s;

	  // Sort by id
	  double difference = this.x - pt2.x;
		if (difference == 0) {
			difference = this.y - pt2.y;
		}
		if (difference == 0)
		  return 0;
		return difference > 0 ? 1 : -1;
	}
	
	public boolean equals(Object obj) {
		PointTIN r2 = (PointTIN) obj;
		return this.x == r2.x && this.y == r2.y;
	}
	
	public double distanceTo(PointTIN s) {
		double dx = s.x - this.x;
		double dy = s.y - this.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	@Override
	public PointTIN clone() {
	  return new PointTIN(this.quadIndex,this.pointIDStr, this.x, this.y);
	}
	

  @Override
  public Rectangle getMBR() {
    return new Rectangle(x, y, x, y);
  }

  @Override
  public double distanceTo(double px, double py) {
    double dx = x - px;
    double dy = y - py;
    return Math.sqrt(dx * dx + dy * dy);
  }
  
  
  //Detect if the two shapes intersect with each other.
  @Override
  public boolean isIntersected(Shape s) {
    return getMBR().isIntersected(s);
  }
  
  
  @Override
  public String toString() {
    return "PointTIN: " +quadIndex + " " + pointIDStr + " "+ x+" "+y;
  }
  
  
  @Override
  public Text toText(Text text) {
//    TextSerializerHelper.serializeDouble(x, text, ',');
//    TextSerializerHelper.serializeDouble(y, text, '\0');
	  String str="";
	  str += this.quadIndex + " ";
	  
	  str += this.pointIDStr + " ";
	  str += this.x + " ";
	  str += this.y;
	  
	  Iterator<String> ite = triangleList.iterator();
	  
	  while(ite.hasNext())
	  {
		  str += " " + ite.next();
	  }
	 
	  text.set(str);
	  
    return text;
  }
  
  
  @Override
  public void fromText(Text text) {
//    x = TextSerializerHelper.consumeDouble(text, ',');
//    y = TextSerializerHelper.consumeDouble(text, '\0');
	  String[] strs = text.toString().split(" ");
	  this.quadIndex = strs[0];
	  this.pointIDStr = strs[1];
	  this.x = Double.parseDouble(strs[2]);
	  this.y = Double.parseDouble(strs[3]);
	  
	  triangleList.clear();
	  for(int i=4;i<strs.length;i++)
	  {
		  triangleList.add(strs[i]);
	  }
	  
  }


@Override
public void draw(Graphics g, Rectangle fileMBR, int imageWidth,
		int imageHeight, boolean vflip, double scale) {
	
}

//  @Override
//  public void draw(Graphics g, Rectangle fileMBR, int imageWidth,
//  		int imageHeight, boolean vflip, double scale) {
//    int imageX = (int) Math.round((this.x - fileMBR.x1) * imageWidth / fileMBR.getWidth());
//    int imageY = (int) Math.round(((vflip? -this.y : this.y) - fileMBR.y1) * imageHeight / fileMBR.getHeight());
//    g.fillRect(imageX, imageY, 1, 1);  	
//  }

}
