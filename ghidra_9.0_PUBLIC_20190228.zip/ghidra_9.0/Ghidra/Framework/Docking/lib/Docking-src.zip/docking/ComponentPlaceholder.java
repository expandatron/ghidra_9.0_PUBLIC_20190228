/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package docking;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import docking.action.DockingAction;
import docking.action.DockingActionIf;
import ghidra.util.exception.AssertException;

/**
 * Class to hold information about a dockable component with respect to its position within the
 * windowing system.  It also holds identification information about the provider so that its
 * location can be reused when the provider is re-opened.
 */
public class ComponentPlaceholder {
	private String name;
	private String owner;
	private String group;
	private String title;
	private String subTitle;
	private String tabText;
	private Icon icon;
	private ComponentProvider componentProvider;
	private List<DockingActionIf> actions;
	private boolean isShowing;
	private ComponentNode compNode;
	private DockableComponent comp;
	private boolean showHeader = true;
	private boolean disposed = false;

	/** Set to a default value if this class' provider is not duplicatable; a unique value otherwise */
	private long instanceID = 0;

	ComponentPlaceholder(ComponentProvider provider) {
		this.componentProvider = provider;
		updateInfo(provider);
		this.actions = new ArrayList<>();

		instanceID = provider.getInstanceID();
	}

	/**
	 * XML Constructor!!!!!
	 * @param name the name of the component.
	 * @param owner the owner of the component.
	 * @param show whether or not the component is showing.
	 * @param node componentNode that has this placeholder.
	 */
	ComponentPlaceholder(String name, String owner, String group, String title, boolean show,
			ComponentNode node, long instanceID) {

		this.name = name;
		this.owner = owner;
		this.title = title;
		this.isShowing = show;
		this.group = group;
		this.compNode = node;
		this.instanceID = instanceID;
		this.actions = new ArrayList<>();
	}

	long getInstanceID() {
		return instanceID;
	}

	/**
	 * Returns the componentNode containing this placeholder.
	 */
	ComponentNode getNode() {
		return compNode;
	}

	void showHeader(boolean b) {
		this.showHeader = b;
		invalidateComponentNode();
	}

	boolean isHeaderShowing() {
		return showHeader;
	}

	/**
	 * Sets the componentNode containing this placeholder
	 * @param node the component node containing this placeholder.
	 */
	void setNode(ComponentNode node) {
		compNode = node;
	}

	boolean isParented() {
		return compNode != null;
	}

	/**
	 * Returns true if the component is not hidden.
	 */
	boolean isShowing() {
		return isShowing && componentProvider != null;
	}

	/**
	 * True signals that the showing state of the placeholder is true.  This is used to re-show
	 * providers after restoring from XML.
	 * @return True signals that the showing state of the placeholder is true
	 */
	boolean wantsToBeShowing() {
		return isShowing;
	}

	private void invalidateComponentNode() {
		if (compNode != null) {
			compNode.invalidate();
		}
	}

	String getGroup() {
		if (componentProvider != null) {
			group = getGroup(componentProvider);
		}
		return group;
	}

	DetachedWindowNode getWindowNode() {
		Node node = compNode.parent;
		while (node != null) {
			if (node instanceof DetachedWindowNode) {
				return (DetachedWindowNode) node;
			}
			node = node.parent;
		}
		return null;
	}

	WindowNode getTopLevelNode() {
		if (compNode == null) {
			return null;
		}
		return compNode.getTopLevelNode();
	}

	/**
	 * Call to signal that this placeholder is not being used.  This is needed when placeholders
	 * are restored from XML as being visible, but then no provider can be found for them.
	 */
	void reset() {
		isShowing = false;
		invalidate();
	}

	/**
	 * Sets the active state.
	 * @param doShow true to show the component, false to hide it.
	 */
	void show(boolean doShow) {
		if (doShow == isShowing) {
			return;
		}

		isShowing = doShow;
		invalidate();
	}

	private void invalidate() {
		invalidateComponentNode();

		disposeComponent();

		if (componentProvider != null) {
			if (isShowing) {
				componentProvider.componentShown();
			}
			else {
				componentProvider.componentHidden();
			}
		}
	}

	void dispose() {
		disposed = true;

		if (comp != null) {
			comp.dispose();
			comp = null;
		}

		if (compNode == null) {
			return;
		}

		WindowNode windowNode = compNode.getTopLevelNode();
		if (windowNode != null) {
			windowNode.componentRemoved(this);
		}

		compNode.remove(this);
	}

	private void disposeComponent() {
		if (comp == null) {
			return;
		}

		comp.dispose();
		comp = null;
	}

	void toFront() {
		if (comp != null) {
			compNode.makeSelectedTab(this);
		}

	}

	/**
	 * Requests focus for the component associated with this placeholder.
	 */
	void requestFocus() {
		if (comp != null) {
			compNode.makeSelectedTab(this);
			activateWindow();

			// make sure the tab has time to become active before trying to request focus
			comp.requestFocus();
			final Component tmp = comp;// put in temp variable in case another thread deletes it
			SwingUtilities.invokeLater(() -> {
				if (tmp != null) {
					tmp.requestFocus();
				}
			});
		}
	}

	// makes sure that the given window is not in an iconified state
	private void activateWindow() {
		DetachedWindowNode windowNode = getWindowNode();
		if (windowNode != null) {
			Window window = getWindowNode().getWindow();
			if (window instanceof Frame) {
				Frame frame = (Frame) window;
				frame.setState(Frame.NORMAL);
			}
		}
	}

	/**
	 * Hides the component associated with this placeholder and notifies the provider that
	 * the component has been hidden.
	 *
	 */
	void close() {
		if (componentProvider != null) {
			componentProvider.closeComponent();
		}
	}

	/**
	 * Returns a Dockable component that wraps the component for this placeholder.
	 */
	public DockableComponent getComponent() {
		if (disposed) {
			throw new AssertException(
				"Attempted to get a component for a disposed component placeholder");
		}
		boolean isDocking = true;
		if (compNode != null) {
			isDocking = compNode.winMgr.isDocking();
		}
		if (comp == null && isShowing) {
			comp = new DockableComponent(this, isDocking);
		}
		return comp;
	}

	void setIcon(Icon icon) {
		icon = DockingUtils.scaleIconAsNeeded(icon);

		this.icon = icon;
		if (comp != null) {
			comp.setIcon(icon);
		}
		if (compNode != null) {
			compNode.iconChanged(this);
		}
	}

	Icon getIcon() {
		return icon;
	}

	/**
	 * Returns the title for this component.
	 * @return the title for this component.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Returns the subtitle for the component
	 * @return the subtitle for the component
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * The text for display on the tab of a tabbed component.
	 * @return The text for display on the tab of a tabbed component.
	 */
	public String getTabText() {
		return tabText;
	}

	/**
	 * Returns the name of this component.
	 * @return the name of this component.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the owner for the component.
	 */
	String getOwner() {
		return owner;
	}

	/**
	 * Returns the component associated with this placeholder.
	 */
	JComponent getProviderComponent() {
		if (componentProvider != null) {
			return componentProvider.getComponent();
		}
		return new JPanel();
	}

	/**
	 * Returns true if this placeholder's component is currently in a tabbed pane with other components.
	 */
	boolean isStacked() {
		if (compNode != null) {
			return compNode.isStacked();
		}
		return false;
	}

	/**
	 * Returns true if this placeholder is currently associated with a component. If it is not,
	 * then it exists as a place holder.
	 */
	boolean hasProvider() {
		return componentProvider != null;
	}

	/**
	 * Sets the component provider for this placeholder.
	 */
	void setProvider(ComponentProvider newProvider) {
		this.componentProvider = newProvider;
		actions.clear();
		if (newProvider != null) {
			updateInfo(newProvider);
		}

		disposeComponent();
	}

	public void update() {
		updateInfo(componentProvider);
	}

	private void updateTitle(ComponentProvider provider) {
		title = provider.getTitle();
		subTitle = provider.getSubTitle();
		tabText = provider.getTabText();
		tabText = tabText == null ? title : tabText;

		if (comp != null) {
			comp.setTitle(getFullTitle());
		}
		if (compNode != null) {
			compNode.titleChanged(this);
		}
	}

	private void updateInfo(ComponentProvider updatedProvider) {
		if (updatedProvider == null) {
			return;
		}

		updateTitle(updatedProvider);

		owner = updatedProvider.getOwner();
		name = updatedProvider.getName();
		group = getGroup(updatedProvider);
		setIcon(updatedProvider.getIcon());

		instanceID = componentProvider.getInstanceID();
	}

	private String getGroup(ComponentProvider provider) {
		String providerGroup = provider.getWindowGroup();
		if (providerGroup == null || providerGroup.isEmpty()) {
			return ComponentProvider.DEFAULT_WINDOW_GROUP;
		}
		return providerGroup;
	}

	/**
	 * Returns the component provider for this placeholder.
	 * @return the component provider for this placeholder.
	 */
	public ComponentProvider getProvider() {
		return componentProvider;
	}

	/**
	 * Adds an action to this component
	 * @param action the action to be added.
	 */
	void addAction(DockingActionIf action) {
		if (actions.contains(action)) {
			throw new RuntimeException("Duplicate action added");
		}
		actions.add(action);
		if (comp != null) {
			comp.actionAdded(action);
		}
	}

	/**
	 * Removes an action from this component
	 * @param action the action to be removed.
	 */
	void removeAction(DockingActionIf action) {
		if (actions.remove(action)) {
			if (comp != null) {
				comp.actionRemoved(action);
			}
		}
	}

	/** Updates local actions for providers */
	void contextChanged() {
		ActionContext actionContext = componentProvider.getActionContext(null);
		if (actionContext == null) {
			actionContext = new ActionContext(componentProvider, null);
		}
		for (DockingActionIf action : actions) {
			action.setEnabled(
				action.isValidContext(actionContext) && action.isEnabledForContext(actionContext));
		}
	}

	boolean hasAction(DockingAction action) {
		return actions.contains(action);
	}

	/**
	 * Return iterator over all the local actions defined for this component.
	 */
	Iterator<DockingActionIf> getActions() {
		return actions.iterator();
	}

	/**
	 * notifies the node that this component has focus.
	 *
	 */
	void setSelected(boolean state) {
		if (comp != null) {
			comp.setSelected(state);
			setProviderActivated(state);
		}
	}

	/**
	 * Signals to use the GUI to make this provider stand out from the rest.
	 */
	void emphasize() {
		if (comp != null) {
			comp.emphasize();
		}
	}

	private void setProviderActivated(boolean activated) {
		if (componentProvider == null) {
			return;
		}

		if (activated) {
			componentProvider.componentActivated();
		}
		else {
			componentProvider.componentDeactived();
		}
	}

	/**
	 * Returns the full title for this component (title + subtitle)
	 * @return the full title for this component (title + subtitle)
	 */
	public String getFullTitle() {
		String text = title;
		if (subTitle != null && !subTitle.isEmpty()) {
			text += " - " + subTitle;
		}
		return text;
	}

	@Override
	public String toString() {
		return "name=\"" + name + "\", fullTitle=\"" + getFullTitle() + "\"";
	}
}
