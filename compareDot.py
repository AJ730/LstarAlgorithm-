from networkx import *

G1 = nx_pydot.read_dot("hypothesis_problemPin.dot")
G2 = nx_pydot.read_dot("learnlib-final_problemPin.dot")

print(is_isomorphic(G1, G2, node_match=None, edge_match=None))
