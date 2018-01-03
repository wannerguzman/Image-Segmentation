import java.util.ArrayList;  
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;

import javax.swing.JPanel;

/**
 * The class that will perform the conversion of the image given.
 * It will draw the image to a certain number of colors you want.
 * @author wanner
 *
 */
public class Decomposor extends JPanel {


	/**
	 * Given a disjoint set and a region (defined by its root id), return a list of
	 * adjacent regions (again, represented by their root IDS) get the set rooted at
	 * the root and iterate over it and call the getNeightbors function which
	 * returns an ArrayList of pixels, iterate over the ArrayList and for each index
	 * call call the getID for each pixel call the DisjointSet find the root from
	 * the ID. If the root is different from the one given in the function add the
	 * root to treeSet and if the return root is different add it to TreeSet
	 * 
	 * @param ds
	 * @param root
	 * @return a TreeSet or regions that are neighbors.
	 */
	private TreeSet<Integer> getNeightborSets(DisjointSets<Pixel> ds, int root) {
		TreeSet<Integer> neighbors = new TreeSet<Integer>();
		ArrayList<Pixel> px;
		Set<Pixel> pixels = ds.get(root); // Get the set rooted at the root.
		for (Pixel pixel : pixels) {
			px = getNeightbors(pixel); // Get an arrayList of pixels.
			for (Pixel pixelNeightbor : px) {
				int neighborRoot = ds.find(getID(pixelNeightbor));
				if (neighborRoot != root) {
					neighbors.add(neighborRoot);
				}
			}
		}
		// Garbage
		px = null;
		pixels = null;
		return neighbors;
	}

	/**
	 * Given two regions R1 and R2, compute the similarity between these two regions.
	 * You will need to compute the average color, C, of the union of these two
	 * regions. First I get the sets from both roots next I compute the average color
	 * of both sets. Next I compute the Average color between both Average colors
	 * obtained and compute the sum of the color differences between C and all pixels
	 * in R1 and R2.
	 * From this I can now create a Similarity and return it.
	 * @param ds
	 * @param root1
	 * @param root2
	 * @return A similarity between root1 and root2
	 */
	private Similarity getSimilarity(DisjointSets<Pixel> ds, int root1, int root2) {
		Set<Pixel> s1 = ds.get(root1);
		Set<Pixel> s2 = ds.get(root2);

		Color c1 = computeAverageColor(s1);
		Color c2 = computeAverageColor(s2);

		int c_red = ((c1.getRed() * s1.size()) + (c2.getRed() * s2.size())) / (s1.size() + s2.size());
		int c_green = ((c1.getGreen() * s1.size()) + (c2.getGreen() * s2.size())) / (s1.size() + s2.size());
		int c_blue = ((c1.getBlue() * s1.size()) + (c2.getBlue() * s2.size())) / (s1.size() + s2.size());
		Color c = new Color(c_red, c_green, c_blue);

		int sum = 0; // Sum variable.
		for (Pixel pixel : s1) { // Add the difference to sum.
			sum += getDifference(c, getColor(pixel));
		}
		for (Pixel pixel : s2) { // Add the difference to sum.
			sum += getDifference(c, getColor(pixel));
		}
		// Garbage
		s1 = null;
		s2 = null;
		c1 = null;
		c2 = null;
		Similarity similarity = new Similarity(sum, getPixel(root1), getPixel(root2));
		return similarity;
	}

	/**
	 * High-Level idea - Iteratively merging two adjacent regions with most similar
	 * colors until the number of regions is K. I first fill up the DisjointSet ds
	 * with all possible pixels. Then I create a PriorityQueue<Similarity>.
	 * For each pixel using the ArrayList of pixels I created to create my DsijointSet, 
	 * I iterate though all pixels and for each pixel got its Neighbors. Iterate over 
	 * them and create and add a Similarity between the pixel and its Neighbors. I then 
	 * loop until the number of sets in DisjointSet is k. I get a Similarity and if the pair of pixels are not
	 * disjoint sets continue and grab another Similarity else if roots are roots of
	 * their own set, get the pair of Pixels, iterate neighbors and create
	 * and add a new Similarity else if distance of Similarity is 0 union roots else if
	 * Similarity is>0 then add it to queue.
	 * 
	 * @param K
	 */
	public void segment(int K) // K is the number of desired segments.
	{
		if (K < 2) {
			throw new IllegalArgumentException(new String("! Error: K should be greater than 1, current K=" + K));
		}

		int width = this.image.getWidth();
		int height = this.image.getHeight();
		// Hint: the algorithm is not fast and you are processing many pixels.
		// (e.g., 10,000 pixel for a small 100 by 100 image)
		// Output a "." Every 100 unions so you get some progress updates.
		ArrayList<Pixel> pixels = new ArrayList<Pixel>(); // Create arrayLsit of pixels.
		// Add all possible pixels to arrayList.
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				pixels.add(new Pixel(w, h));
			} // end for w
		} // end for h
		ds = new DisjointSets<Pixel>(pixels); // Create DisjointSet of pixels.
		PriorityQueue<Similarity> s = new PriorityQueue<Similarity>(); // Create a queue of Similarity.
		for (Pixel p : pixels) { // Loop through each pixel.
			ArrayList<Pixel> px = getNeightbors(p); // Get its neighbors.
			for (Pixel pixelNeightbor : px) { // Loop through the neighbors.
				s.add(getSimilarity(ds, getID(p), getID(pixelNeightbor))); // Get the similarity between pixel and
																			// neighbor.
			} // and add it to the queue.
			px = null;
		}
		// Garbage
		pixels = null;

		while (ds.getNumSets() != K) {
			tell_progress(K);
			Similarity sim = s.remove(); // Removes the pair of most similar regions.
			int id1 = getID(sim.pixels.p);
			int setid1 = ds.find(id1);
			int id2 = getID(sim.pixels.q);
			int setid2 = ds.find(id2); // If the pixels are both roots of their own sets.
			if (setid1 == setid2)
				continue; // If the regions are not disjoint ignore and continue to and remove next queue.
			if (id1 == setid1 && id2 == setid2) {
				if (sim.compareTo(getSimilarity(ds, setid1, setid2)) != 0)
					continue;
				int root = ds.union(setid1, setid2);
				if (sim.distance == 0)
					continue;
				TreeSet<Integer> Neightbors = getNeightborSets(ds, root);
				for (Integer NeightborRoot : Neightbors) {
					s.add(getSimilarity(ds, root, NeightborRoot));
				}
				// Garbage
				Neightbors = null;
			} else { // If the pixels are not roots of their own set.
				if (sim.distance > 0) { // If distance is greater than 0.
					s.add(getSimilarity(ds, setid1, setid2)); // Re-add the Similarity we removed back to queue s.
				} else if (sim.distance == 0) { // If distance is 0.
					ds.union(setid1, setid2); // Compute the union of both pixels.
				}
			}
		}
	}

	/**
	 * Print progress to the screen given K.
	 * @param K
	 */
	public String tell_progress(int K) // K is the same as in segment(integer K)
	{
		float progress = (100.0f * K) / ds.getNumSets();
		int p = (int) Math.floor(progress);
		String str = "Progress: " + String.format("%.02f", progress) + "% \r";
		System.err.print(str);
		return str;
	}

	/**
	 * Recolors all pixels with the average color and save output image.
	 * Iterate over those sets and get
	 * the average then iterate over the pixels, and set the color.
	 * 
	 * @param K
	 */
	public void outputResults(int K) {
		// Collect all sets.
		int region_counter = 1;
		ArrayList<Pair<Integer>> sorted_regions = new ArrayList<Pair<Integer>>();

		int width = this.image.getWidth();
		int height = this.image.getHeight();
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int id = getID(new Pixel(w, h));
				int setid = ds.find(id);
				if (id != setid)
					continue;
				sorted_regions.add(new Pair<Integer>(ds.get(setid).size(), setid));
			} // end for w
		} // end for h

		// Sort the regions.
		Collections.sort(sorted_regions, new Comparator<Pair<Integer>>() {
			@Override
			public int compare(Pair<Integer> a, Pair<Integer> b) {
				if (a.p != b.p)
					return b.p - a.p;
				else
					return b.q - a.q;
			}
		});

		// Re-color and output region info.
		int i = 1;
		for (Pair<Integer> set : sorted_regions) { // Iterate of sets are in a pair. the pair contains a size and root.
													// Pair(size,root)
			int setid = set.q; // For each set get the id.
			int size = set.p;
			Set<Pixel> pixels = ds.get(setid); // Get the set of pixels for that ID.
			Color c = computeAverageColor(pixels); // Compute the color of the pixel set.
			for (Pixel pixel : pixels) { // Iterate through the pixels and set the color.
				image.setRGB(pixel.p, pixel.q, c.getRGB());
			}
			System.out.println("region" + " " + i + " " + "size= " + size + " " + "color=" + c.toString());
			i++;
		}
		// Hint: Use image.setRGB(x,y,c.getRGB()) to change the color of a pixel (x,y).
		// To the given color "c".

		// Save output image.
		String out_filename = img_filename + "_seg_" + K + ".png";
		try {
			File ouptut = new File(out_filename);
			ImageIO.write(this.image, "png", ouptut);
			System.err.println("- Saved result to " + out_filename);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + out_filename);
		}
	}



	//
	// Data
	//
	public BufferedImage image; // This is the 2D array of RGB pixels.
	private String img_filename; // Input image filename without .jpg or .png.
	private DisjointSets<Pixel> ds; // The disjoint sets.


	/**
	 * Decomposer Constructor that reads a file from the name and store it into image.
	 * @param imgfile
	 */
	public Decomposor(String imgfile) {
		File imageFile = new File(imgfile);
		try {
			this.image = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + imgfile + ", error msg: " + e);
			return;
		}
		this.img_filename = imgfile.substring(0, imgfile.lastIndexOf('.')); // remember the filename
	}

	//
	// 3 private classes below.
	//
	
	/**
	 * private class pair. This is how we will store our pixels, in pair(w,h).
	 * 
	 * @author wanner
	 *
	 * @param <T>
	 */
	private class Pair<T> {
		/**
		 * Store the pair with the given parameters from this constructor.
		 * @param p_
		 * @param q_
		 */
		public Pair(T p_, T q_) {
			this.p = p_;
			this.q = q_;
		}

		T p, q;
	}

	/**
	 * A pixel is a 2D coordinate (w,h) in an image.
	 * 
	 * This class extends the pair into a Pixel.
	 * 
	 * @author wanner
	 *
	 */
	private class Pixel extends Pair<Integer> {
		/**
		 * Calls super class pair and store pixels calling its constructor.
		 * 
		 * @param w
		 * @param h
		 */
		public Pixel(int w, int h) {
			super(w, h);
		}
	} // Aliasing Pixel.


	/**
	 * This class represents the similarity between the colors of two adjacent
	 * pixels or regions.
	 * This is performed be creating a Similarity class.
	 * Similarity has a distance it is similar to and the pair of pixels we are comparing.
	 * @author wanner
	 *
	 */
	private class Similarity implements Comparable<Similarity> {
		

		// A pair of adjacent pixels or regions (represented by the "root" pixels).
		public Pair<Pixel> pixels;

		// Distance between the color of two pixels or two regions,
		// smaller distance indicates higher similarity.
		public int distance;
		
		/**
		 * A similarity has a distance and pixel.
		 * Update it using this constructor.
		 * @param d
		 * @param p
		 * @param q
		 */
		public Similarity(int d, Pixel p, Pixel q) {
			this.distance = d;
			this.pixels = new Pair<Pixel>(p, q);
		}
		/**
		 * Returns the similarity between two Similarities.
		 * Use the compareTo method from java.
		 * Will return 0 when they both have the same distance.
		 * @param Similarity other
		 * @return Integer
		 */
		public int compareTo(Similarity other) {
			int diff = this.distance - other.distance;
			if (diff != 0)
				return diff;
			diff = getID(this.pixels.p) - getID(other.pixels.p);
			if (diff != 0)
				return diff;
			return getID(this.pixels.q) - getID(other.pixels.q);
		}
	}

	//
	// Helper functions.
	//

	// Convert a pixel to an ID.
	/**
	 * Method the gets the ID based on the pixel.
	 * 
	 * @param pixel
	 * @return Integer that defines the ID of pixel.
	 */
	private int getID(Pixel pixel) {
		return this.image.getWidth() * pixel.q + pixel.p;
	}

	// Convert ID back to pixel.
	/**
	 * 
	 * @param id
	 * @return the Pixel from a given ID.
	 */
	private Pixel getPixel(int id) {
		int h = id / this.image.getWidth();
		int w = id - this.image.getWidth() * h;

		if (h < 0 || h >= this.image.getHeight() || w < 0 || w >= this.image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(w, h);
	}
	/**
	 * 
	 * @param p
	 * @return The color of the pixel.
	 */
	private Color getColor(Pixel p) {
		return new Color(image.getRGB(p.p, p.q));
	}

	
	/**
	 * Compute the average color of a collection of pixels.
	 * @param pixels
	 * @return A new color computed from the average color of all pixels.
	 */
	private Color computeAverageColor(AbstractCollection<Pixel> pixels) {
		int r = 0, g = 0, b = 0;
		for (Pixel p : pixels) {
			Color c = new Color(image.getRGB(p.p, p.q));
			r += c.getRed();
			g += c.getGreen();
			b += c.getBlue();
		}
		return new Color(r / pixels.size(), g / pixels.size(), b / pixels.size());
	}
	/**
	 * 
	 * @param c1
	 * @param c2
	 * @return an Integer representing the difference between both calories given.
	 */
	private int getDifference(Color c1, Color c2) {
		int r = (int) (c1.getRed() - c2.getRed());
		int g = (int) (c1.getGreen() - c2.getGreen());
		int b = (int) (c1.getBlue() - c2.getBlue());

		return r * r + g * g + b * b;
	}

	/**
	 * 8-neighbors of a given pixel.
	 * Gets the neighbors of the pixel give
	 * Pretty much looks at the pixel in the image and any pixel around it is stored in a ArrayList.
	 * @param pixel
	 * @return ArrayList of adjacent pixels to the given one in the parameter.
	 */
	private ArrayList<Pixel> getNeightbors(Pixel pixel) {
		ArrayList<Pixel> neighbors = new ArrayList<Pixel>();

		for (int i = -1; i <= 1; i++) {
			int n_w = pixel.p + i;
			if (n_w < 0 || n_w == this.image.getWidth())
				continue;
			for (int j = -1; j <= 1; j++) {
				int n_h = pixel.q + j;
				if (n_h < 0 || n_h == this.image.getHeight())
					continue;
				if (i == 0 && j == 0)
					continue;
				neighbors.add(new Pixel(n_w, n_h));
			} // end for j
		} // end for i

		return neighbors;
	}
	
	/**
	 * Method to paint new image
	 */
	public void paint(Graphics g) {
		g.drawImage(this.image, 0, 0, this);
	}
}