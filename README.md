# Locomotif
### Source code and binary for Locomotif project

Here are the sources for the locomotif project that correspond to my work within the project.
I'm also including the executable jar of that version. 
An updated version of the project can still be found at 
http://bibiserv.techfak.uni-bielefeld.de/locomotif.
To my knowledge, the underlying ADP language has changed and was updated to the new version.
However, the server side of the software doesn't seem to function anymore.
So, while one can still design motifs and see the translated grammar via the 
File->Export tab, it is not possible to generate actual matchers on the server anymore.

- The overall structure can be described as a GUI interface holding visual building blocks. Based on 
an abstract RnaShape and a parent BuildingBlock class, each type of building block such as hairpin loop, stem etc
has an extension for each of these classes defining the visual appearance (Shape) and the data logic plus editing interfaces to add annotation details.
- The Magnet and FreeMagnets classes deal with mechanisms of attaching building blocks to each other.
- Translator and ADPTranslator are responsible for the underlying translation of building blocks into other grammars such as XML or ADP
