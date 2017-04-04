/*
 * Copyright Jurong Port Pte Ltd
 * Created on Nov 20, 2007
 */
package org.latticesoft.util.container.tree.util;

import org.latticesoft.command.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.container.tree.Node;
import org.latticesoft.util.container.tree.impl.NodeImpl;

public class TestTree implements Command {
	
	public Object execute(Object o) throws CommandException {
		this.buildTree();
		return null;
	}
	
	public Node buildTree() {
		Node n = null;
		n = new NodeImpl();
		
		return n;
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0 || args[0] == null) {
			System.out.println("Usage: java org.latticesoft.util.container.tree.TestTree <<properties>>");
			return;
		}
		PropertyMap.singletonize(args[0]);
		TestTree app = new TestTree();
		app.execute(null);
	}
	
}
