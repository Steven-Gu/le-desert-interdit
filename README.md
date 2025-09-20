# Le Désert Interdit (Java)

A Java implementation of the cooperative board game **Forbidden Desert**, developed as a coursework project.

## Project Overview
Players explore a desert to find missing aircraft parts, use equipment, and survive storms in order to escape.  

The project follows an **MVC architecture**:
- **Model**: core game logic (tiles, players, storm cards, equipment)
- **View**: user interface for interacting with the game
- **Controller**: handles commands and propagates updates between model and view
- **Observer pattern**: keeps the view updated when the model changes

## Design
- Abstract classes:
  - `Case`: parent for different terrain types
  - `Player`: parent for different characters with unique skills
  - `Carte_Tempete`: storm/weather cards
  - `Equipement`: equipment cards
- Task split:
  - Hongfei Zhang — players, equipment, view
  - Tianwen Gu — cases, storm cards, model, controller

## Features
- Character selection with special skills
- Tile exploration and part discovery
- Equipment use and storm progression
- Real-time updates of the interface via Observer pattern

## Limitations
- Some actions (e.g. using equipment or climber skill) require **manual coordinate entry** rather than mouse clicks.
- Reachable positions are not highlighted, so players need to know the rules.

## Authors
- Tianwen Gu  
- Hongfei Zhang

