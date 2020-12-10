package algorithms.hybrid;

import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.List;

class ModelNode extends TreeNode {
    List<OWLAxiom> data;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModelNode) {
            ModelNode node = (ModelNode) obj;
            return data.containsAll(node.data) && node.data.containsAll(data);
        }
        return false;
    }
}
