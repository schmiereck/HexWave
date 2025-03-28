# Hexagonale Gitter Simulation

Generiere mir den Java-Code für eine Simulation.

Umgebung:
* Die Simulation soll in einem 2D Gitter aus gleichschenkligen Dreiecken ablaufen, die damit auch ein Hexagonales-Gitter bilden.
* In den Knotenpunkten sollen die Zustände abgelegt werden.
* Ein Knotenpunkt wird immer nur von seinen Nachbarknoten beeinflusst, die über die Kanten des Gitters mit ihm verbunden sind.

Physik:
* Es sollen vereinfachte Quantenmechanische Zustände verwaltet werden. Schwingungen werden als kontinuierliche Werte zwischen minimal- und maximal-Werten dargestellt.
* Alle Zustände sind als Ganzzahlen-Werte (Integer) abgelegt und bewegen sich zischen einem konfigurierbaren minimal-Wert und einem wilkürlich festgelegten maximal-Wert.
* Aufenthaltswahrscheinlichkeit
  * Die Aufenthaltswahrscheinlichkeit einer Anregung (Partikel) wird als kontinuierlicher Wert der jweils von einem minimum zu einem Maximum durchläuft und dann zyklisch wieder beim Minimum beginnt repräsentiert.
  * Die Aufenthaltswahrscheinlichkeit wird als Rotation im Knotenpunkt repräsentiert.
  * Die Aufenthaltswahrscheinlichkeit verteilt sich über alle Knotenpunkte im Gitter. Die Wahrscheinlichkeit ergibt sich aus der Summe der Rotationswinkel (Pfadintegralformulierung der Quantenmechanik nach Richard Feynman).
* Geschwindigkeit
  * Die Geschwindigkeit wird als Rotation im Knotenpunkt repräsentiert.
  * Die Rotation kann asymmetrisch sein, um eine Richtung zu repräsentieren.

Technisches:
* Die Simulation soll in einem Swing JFrame (View) visuell angezeigt werden.
* Für die Berechnungen der Simulation (Calc-Service) und die Ausgabe des aktuellen Zustandes (View) sollen zwei getrennte Threads verwendet werden.
* Der Anzeige Thread holt sich den jeweils aktuellen Zustand über ein Dto-Objekt aus dem Calc-Service.
