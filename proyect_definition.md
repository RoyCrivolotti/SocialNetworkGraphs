# Capstone project scope and problem definition

OVERVIEW

The main objective of this project is to detect sub-communities in any social network using Girvan–Newman’s algorithm for community detection, using Brandes algorithm to calculate edge betweenness on unweighted graphs. This algorithm and their subtly different versions are limited in the data sets they are capable of processing without the problem becoming intractable though, and while it can be generalized to tackle weighted graphs, the performance takes serious hits in the process.

DATA

Two Facebook data sets were chosen for this project, one being the UCSD Facebook data provided by the specialization developers themselves, which consists of about 15k vertices and 450k edges, and the other one being a smaller, hence less demanding to process data set with around 4k nodes and 90k vertices.

QUESTIONS

The easy task that will be addressed is finding the list of second-level friends of a given user ‘x’, with second-level friends being the direct Facebook friends of my friends to whom the user ‘x’ is not directly connected.

The hard problem, and the main theme of the project, is to implement Brandes algorithm for edge-betweenness centrality inside of Girvan-Newman’s algorithm for community detection. After doing some humble research, I’ve learned of the progress made in approximately the last two decades in the study of community-detection algorithms, and I’m now fascinated with the possibilities. The practical implications of being able to detect sub-communities in large and relatively large networks are instinctively huge, but several applications are not obvious, or at least they weren’t to me this past couple of weeks when I first started learning about the topic.

I am considering implementing this version as well as the one that implements Dijkstra’s algorithm, regardless of its poorer performance. The difference in implementing Brandes algorithm with Dijkstra’s algorithm vs. a BFS traversal of the graph is the difference of working with weighted and unweighted edges. Given that I initially wanted to work with a weighted graph, even thoguh it turns out I'm not doing that I want to try and implement it.

ABOUT THE ALGORITHMS

The easy problem can be solved in polynomial time, taking O(n*m) as I iterate over the neighbors of the central node’s neighbors, with n being the size of the central node’s neighbor list and m being the average size of the neighbor’s neighbors list.

As for the hard problem, for the version of the problem dealing with unweighted edges the time complexity of the overall algorithm is O(n*m). However, when working with weighted graphs, the need for a priority queue instead of a normal queue worsens the runtime to O(n*m+n^2*log(n)).

The first obstacle already encountered is that I initially wanted to work with a data set consisting of a month of comments from Reddit. Firstly, I haven’t been able to figure out how to deal with such a large data set, which becomes even scarier considering how costly these algorithms are. Whenever I chose to tackle that challenge and modify my code as I gain more experience, I might do more research and look for approximations that improve the runtime analysis. Louvain's algorithm might be relevant here; when I revisit this in the future I might try this approach and advance on this project further.

A COMMENT ON REDDIT DATA

Initially I wanted to work with a set of Reddit comments, possible because of the hard work of the Reddit user Stuck_In_the_Matrix, who provides such a data set in the form of JSON file format. The data set corresponds to one month of comments from any year after 2005; I wanted to pick a relatively recent month to work with data that was as relevant as possible, given that the Reddit community activity has grown at an impressive speed, giving me reasons to think that by choosing a more recent month I would get more people that are seriously involved/active in Reddit, hence implying the existence of more relationships between users. The first problem encountered here was that I have a limited space for storage in my personal computer, on which I am doing the project, and secondly that the algorithms are extremely costly, and a data set with more than 10k nodes would make the problem intractable according to Newman, and well...this data sets are more than substantial, consisting generally of dozens of millions of input lines (Newman, 2003: p. 14).

The idea was that each node would would represent a user, and there would be a relationship between two users if both posted a comment in the same subreddit, with the weight of the edge increasing by one by each pair of comments that this two users post in the same subreddit, here called a match. To be clear, if both users posted one comment in two different subreddits, then the weight of their relationship would be two. Surely, this was just the first idea on the matter.

Hence, even though there is a version of Brandes algorithm to work with undirected weighted graphs, this algorithm was not an efficient approach to a massive data set. I still want to analyze the existence of sub-communities in Reddit, which are defined here as a set of users that tend to be similarly active in the same subreddits; instead of separating Reddit sub-communities by subreddits, as was my first impulse, I thought of attempting to see if there are sub-communities of users with shared interests that span several subreddits.
However, the algorithms that can manipulate this much information are beyond the scope of this capstone project and this course.

REFERENCES CONSULTED

        •    Brandes, Ulrik, A faster algorithm for betweenness centrality; Journal of Mathematical Sociology 25, 163–177 (2001).
        •    Brandes, Ulrik, On Variants of Shortest-Path Betweenness Centrality and their Generic Computation; Social Networks 30(2):
        136-145 (2008).
        •    Clauset, A., Newman, M. E. J. and C. Moore, Finding community structure in very large networks; Physical Review E,
        70:066111 (2004).
        •    Gjoka, Minas, Kurant, Maciej, Butts Carter T. and Athina Markopoulou, Walking in Facebook: A Case Study of Unbiased
        Sampling of OSNs; Proceedings of IEEE INFOCOM '10; San Diego, CA (2010).
        •    Girvan, M. and M. E. J. Newman, Community structure in social and biological networks, Proc. Natl. Acad. Sci. USA 99, 7821
        7826 (2002).
        •    Girvan, M. and M. E. J. Newman, Finding and evaluating community structure in networks, Phys. Rev. E 69, 026113 (2004).
        •    Green, O., McColl, R. and D.A. Bader, A fast algorithm for streaming betweenness centrality; in Privacy, Security, Risk and
        Trust (PASSAT), International Conference on and 2012 International Confernece on Social Computing (SocialCom) (2012).
        •    Newman, M. E. J., Fast algorithm for detecting community structure in networks; Department of Physics and Center for the
        Study of Complex Systems, University of Michigan; Ann Arbor, MI (2003).
        •    Newman, M. E. J., Scientific collaboration networks: II. Shortest paths, weighted networks, and centrality; Phys. Rev. E
        64, 016132 (2001).
