# Using Genetic Algorithms to Train Neural Networks

## HexGrid Environment
 ````
directions:

      bn  cp     
       \ /
   an---A---ap 
       / \
      cn  bp     
 ````

* GridNode
  * Cell[] cellArr
    * List<Part> partList
  * GridNodeArea[][] gridNodeAreaArr
  * List<GridNodeAreaRef> gridNodeAreaRefList

## ToDo
* use Energie
    * DONE eat other parts
    * DONE death if low/ empty
    * DONE simple energy consumption 
    * energy consumption depending on the complexity and (output-) actions
    * slow down if low
    * reproduction if high
* Add Inputs
  * DONE Neighbour Part-Types
  * Field(s)
  * Energie
* reproduction
  * DONE cloning if population shrinks
  * asexual reproduction
  * sexual reproduction
* Tentacles/ Pull-/Push-Fields
    * Move with
    * Connect/ Release to other Parts
* more complex environment
  * Air, Water, Wallpaper, ...
  * Gravitation
  * Acceleration (Gravitation)
  * Impulse
* Add Outputs
  * Signal-Field
  * Pull-/Push-Fields
* UI
  * Zoom and Scroll
  * Save and Load

## DONE
