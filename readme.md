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
    * eat other parts
    * energy consumption depending on the complexity and (output-) actions
    * slop down if low
    * death if low/ empty
    * reproduction if high
* Add Inputs
  * Field(s)
* reproduction
  * asexual reproduction
  * sexual reproduction
* Tentacles/ Pull-/Push-Fields
    * Move with
    * Connect/ Release to other Parts
* more complex environment
  * Air, Water, Wallpaper, ...
  * Gravitation
* Add Outputs
  * Signal-Field
* UI
  * Zoom and Scroll

## DONE
