package com.huawei.insa2.comm;

import java.util.*;

public abstract class PLayer {

	public static final int maxId = 1000000000;

	protected int id;

	protected int nextChildId;

	protected PLayer parent;

	private Map<Integer, PLayer> children;

	private List<PEventListener> listeners;

	protected PLayer(PLayer theParent) {
		if (theParent != null) {
			id = ++theParent.nextChildId;
			if (theParent.nextChildId >= maxId) {
				theParent.nextChildId = 0;
			}
			if (theParent.children == null) {
				theParent.children = new HashMap<Integer, PLayer>();
			}
			theParent.children.put(id, this);
			parent = theParent;
		}
	}

	public abstract void send(PMessage message) throws PException;

	public void onReceive(PMessage message) {
		PLayer child;
		int childId = getChildId(message);
		if (childId == -1) {
			child = createChild();
			child.onReceive(message);
			fireEvent(new PEvent(PEvent.CHILD_CREATED, this, child));
		} else {
			child = (PLayer) children.get(getChildId(message));
			if (child == null) {
				fireEvent(new PEvent(PEvent.MESSAGE_DISPATCH_FAIL, this, message));
			} else {
				child.onReceive(message);
			}
		}
	}

	public PLayer getParent() {
		return parent;
	}

	public int getChildNumber() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	protected PLayer createChild() {
		throw new UnsupportedOperationException("Not implement");
	}

	protected int getChildId(PMessage message) {
		throw new UnsupportedOperationException("Not implement");
	}

	public void close() {
		if (parent == null) {
			throw new UnsupportedOperationException("Not implement");
		}
		parent.children.remove(new Integer(id));
	}

	public void addEventListener(PEventListener l) {
		if (listeners == null) {
			listeners = new ArrayList<PEventListener>();
		}
		listeners.add(l);
	}

	public void removeEventListener(PEventListener l) {
		listeners.remove(l);
	}

	protected void fireEvent(PEvent e) {
		if (listeners == null) {
			return;
		}

		for (Iterator<PEventListener> i = listeners.iterator(); i.hasNext();) {
			i.next().handle(e);
		}
	}
}
