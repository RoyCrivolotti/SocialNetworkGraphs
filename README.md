# SocialNetworkGraphs
Analyzing social network data using the Graph Data Structure.

Capstone project for the specialization "Object Oriented Java Programming: Data Structures and Beyond Specialization" from UC San Diego (in conjunction with Google) through the Coursera learning platform.

Link to the course: https://www.coursera.org/specializations/java-object-oriented

The first task is a warm-up exercise where I was supposed to design a graph representation in Java; how to design this graph was entirely up to me:

      – Design and implement a set of classes to store data as a graph (this graph must implement the provided interface in the base code, and the class that implements this interface must be named CapGraph). At least have a method to add edges and vertices.

      – Write a method that extracts a subgraph (called an "egonet") from the Facebook data provided in the base code. An egonet is a subgraph that includes the vertex center and all of the vertices, v_i, that are directly connected by an edge from center to v_i and all of the edges between these vertices that are present in the original graph. Examples of egonets are given in the lectures in this module.If the vertex center is not present in the original graph, this method should return an empty Graph.

      – Write a method that finds the strongly connected components (SCCs) in a directed graph.

After the warm up assignment, I could either extend my work on Egonets or SCCs to build up the capstone project, or I could choose a completely new avenue for my project.

The backbone of the project is to pick two questions, an easy one that can be answered without much more investigation than that which I did for the warm-up exercise, and a more complex one for which I am to do research in books/published papers/wherever, be that to look for questions/problems that interest me or, after I have chosen my question, to read on algorithms that might inspire me on how to write my project. There is another text file in which I define the scope of my capstone project, including the definition of the data I choose to use and base my project on, the questions I will investigate and the algorithms and data structures I'll use to investigate these questions, including an analysis of the appropriateness and limitations of these algorithms.

//

Starter code:
      
      – src/graph: this package contained two files: Graph.java, the graph interface I had to implement for this assignment, and CapGraph.java, the definition of the class that implements the Graph interface (CapGraph.java was basically empty). The graders provided create instances of CapGraph to test my implementation.
      
      – src/graph/grader: the graph.grader package contained the code they use to grade my CapGraph implementation at the point of submission. The point of having them myself is to test my code before submitting the assignment.
      
      – src/util: the util package contained only one file: GraphLoader.java, a utility class written by the developers of the course; it has a method to load the data files they provide for the warm-up into a Graph object that implements the Graph interface.
      
      – data: the root data folder contained a number of files. The data/data_source.txt gives the source of the data files, and the data/README file provides more information about the files in that folder. This files were not to be modified as many were used for grading.

Every file that is not those detailed in the 'starter code' section, be that a class or a data file, is something added by me. Also, this CapGraph class was authored by me.
