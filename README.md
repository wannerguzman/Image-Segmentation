# Image-Segmentation
```
Program to segment an image into K>1 regions, where K is a user parameter. The program 
takes a single bitmap RGB image in jpg or png formats, positive integer K > 1, i.e., 
the number of final regions. The program than outputs a list of regions ordered from 
large to small (pixel count). The list will contain the size of each region and the 
color. It will also output an image file, in which, all pixels in the same region are 
assigned the average color of the region. The image filename must be named ABCD_seg_K.png 
if your input file is "ABCD.xyz" where xyz is jpg or png and K is the input argument K. 
For this example, I segmentation for the original image when K was 10 and 5.
```
# Java Classes
1. ImgSeg
```
This is the class with the main method to run the program.
```
2. Decomposer
```
This class performs the entire functionalities of the program. It finds the average color
of a certain pixel. It also finds neighbors of a pixel. It creates a queue with similarities
from the average color and neighbors. It also constructs the main disjoint set and
performs union when a similarity is found and recolors the pixels.

```
3. Disjoint Set
```
Class in where I define my disjoint sets. I have methods to perform a union by size, find
using path compresion, and get to get a set.

```
4. Set
```
Created a simple set class like a linked list. Have an inner class node. Have methods to add
to data to the set and add an entire set to the current set. Also have some helper methods to get a 
set, clear the entire set. Iteration takes place using an iterator inside the set class.

```
5. PriorityQueue
```
Defend a queue. Just to recap when you add data to a queue it adds it at the end. When you remove
data you remove from the front. For this data structure I did not use node I used an array with a
default size. Size is increased when a limit is reached. You also have functionalities like percolate Down,
build Heap, checking if it is empty, clear the queue. Iteration is also performed using an iterator.

```

# Compile
```
You can create a folder Snake-Game add all the source code and images provided in the Game folder.
Go to terminal and into the directory of the File created with the source code. You can also create
a folder in the same directory of the program with images you wish to choose, but that is just an option. It
makes it easier to write the path of the image when running the program. you run the program with the following 
command in the java ImgSeg -k 5 bird.png where java is needed to run a class then following the space you 
need the class name following by another space and with -k. -k is predefined in in ImgSeg class. followed 
by that you then enter the number of segmentation greater than 1. Then followed by another space and the
path of the image you wish to perform segmentation on.

```
# Example Run
## original image
![Screenshot](Segmentation/bird.JPG)

1. Segment into 5 regions

```
> java ImgSeg -k 5 bird.jpg
region 1 size= 42384 color=java.awt.Color[r=120,g=89,b=14]
region 2 size= 6992 color=java.awt.Color[r=177,g=109,b=56]
region 3 size= 4477 color=java.awt.Color[r=44,g=48,b=70]
region 4 size= 3371 color=java.awt.Color[r=197,g=159,b=143]
region 5 size= 2976 color=java.awt.Color[r=123,g=156,b=180]
- Saved result to bird_seg_5.png
```
2. Segment into 10 regions

```
> java ImgSeg -k 10 bird.jpg
region 1 size= 22293 color=java.awt.Color[r=123,g=93,b=3]
region 2 size= 9344 color=java.awt.Color[r=116,g=87,b=21]
region 3 size= 6778 color=java.awt.Color[r=115,g=73,b=18]
region 4 size= 4477 color=java.awt.Color[r=44,g=48,b=70]
region 5 size= 3969 color=java.awt.Color[r=116,g=100,b=47]
region 6 size= 3570 color=java.awt.Color[r=158,g=60,b=42]
region 7 size= 3422 color=java.awt.Color[r=198,g=160,b=70]
region 8 size= 2976 color=java.awt.Color[r=123,g=156,b=180]
region 9 size= 2007 color=java.awt.Color[r=204,g=124,b=105]
region 10 size= 1364 color=java.awt.Color[r=188,g=210,b=198]
- Saved result to bird_seg_10.png
```
