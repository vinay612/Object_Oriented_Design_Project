import tkinter
import networkx as nx
import matplotlib.pyplot as plt

G=nx.Graph()
G.add_nodes_from(["a",'b','c','d','e','q'])
G.add_edges_from([('a','b'),('c','d'),('e','a'),('q','a'),('a','c')])
G.edges['a','b']['color']='r'
G.add_edge('w','c',color='r')
G.node['a']['color']='red'
window = tkinter.Tk()
window.title("JGrpahED")
window.geometry("612x500")

def function():
    pass

# creating a root menu to insert all the sub menus
root_menu = tkinter.Menu(window)
window.config(menu = root_menu)

# creating sub menus in the root menu
file_menu = tkinter.Menu(root_menu) # it intializes a new su menu in the root menu
root_menu.add_cascade(label = "File", menu = file_menu) # it creates the name of the sub menu
file_menu.add_command(label = "New Graph.....", command = function) # it adds a option to the sub menu 'command' parameter is used to do some action
file_menu.add_command(label = "Open Graph", command = function)
file_menu.add_command(label = "Save Graph", command = function)
file_menu.add_separator() # it adds a line after the 'Open files' option
file_menu.add_command(label = "Exit", command = window.quit)

#creating a node sub-menu
node_menu = tkinter.Menu(root_menu)
root_menu.add_cascade(label = "Node", menu = node_menu)
node_menu.add_command(label = "Add a Node", command = function)
node_menu.add_command(label = "Delete a Node", command = function)
node_menu.add_command(label = "Move a Node", command = function)

#creating an edge sub-menu
edge_menu = tkinter.Menu(root_menu)
root_menu.add_cascade(label = "Edge", menu = edge_menu)
edge_menu.add_command(label = "Add an Edge", command = function)
edge_menu.add_command(label = "Delete a Edge", command = function)
edge_menu.add_command(label = "Move a Selected Edge", command = function)

nx.draw(G)
plt.savefig("graph.png")







logo = tkinter.PhotoImage(file="graph.png")

w1 = tkinter.Label(window, image=logo).pack()

window.mainloop()