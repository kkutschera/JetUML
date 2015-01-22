/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.stg.jetuml;

import ca.mcgill.cs.stg.jetuml.graph.ActorNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.UseCaseNode;


/**
   A UML use case diagram.
*/
public class UseCaseDiagramGraph extends Graph
{

   public Node[] getNodePrototypes()
   {
      return NODE_PROTOTYPES;
   }

   public Edge[] getEdgePrototypes()
   {
      return EDGE_PROTOTYPES;
   }
   
   private static final Node[] NODE_PROTOTYPES = new Node[3];

   private static final Edge[] EDGE_PROTOTYPES = new Edge[5];

   static
   {
      NODE_PROTOTYPES[0] = new ActorNode();
      NODE_PROTOTYPES[1] = new UseCaseNode();
      NODE_PROTOTYPES[2] = new NoteNode();

      ClassRelationshipEdge communication =
         new ClassRelationshipEdge();
      communication.setBentStyle(BentStyle.STRAIGHT);
      communication.setLineStyle(LineStyle.SOLID);
      communication.setEndArrowHead(ArrowHead.NONE);
      EDGE_PROTOTYPES[0] = communication;

      ClassRelationshipEdge extendRel =
         new ClassRelationshipEdge();
      extendRel.setBentStyle(BentStyle.STRAIGHT);
      extendRel.setLineStyle(LineStyle.DOTTED);
      extendRel.setEndArrowHead(ArrowHead.V);
      extendRel.setMiddleLabel("\u00ABextend\u00BB");
      EDGE_PROTOTYPES[1] = extendRel;

      ClassRelationshipEdge includeRel =
         new ClassRelationshipEdge();
      includeRel.setBentStyle(BentStyle.STRAIGHT);
      includeRel.setLineStyle(LineStyle.DOTTED);
      includeRel.setEndArrowHead(ArrowHead.V);
      includeRel.setMiddleLabel("\u00ABinclude\u00BB");
      EDGE_PROTOTYPES[2] = includeRel;
      
      ClassRelationshipEdge generalization =
         new ClassRelationshipEdge();
      generalization.setBentStyle(BentStyle.STRAIGHT);
      generalization.setLineStyle(LineStyle.SOLID);
      generalization.setEndArrowHead(ArrowHead.TRIANGLE);
      EDGE_PROTOTYPES[3] = generalization;

      EDGE_PROTOTYPES[4] = new NoteEdge();
   }
}





