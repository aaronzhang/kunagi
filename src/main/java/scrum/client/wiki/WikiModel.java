package scrum.client.wiki;

import java.util.ArrayList;
import java.util.List;

public class WikiModel {

	private List<AWikiElement> elements = new ArrayList<AWikiElement>();

	public void add(AWikiElement element) {
		elements.add(element);
	}

	public String toHtml(HtmlContext context) {
		StringBuilder sb = new StringBuilder();
		for (AWikiElement element : elements) {
			sb.append(element.toHtml(context));
		}
		return sb.toString();
	}

	public List<AWikiElement> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WikiModel(");
		for (AWikiElement element : elements) {
			sb.append("\n    ").append(element);
		}
		sb.append("\n)");
		return sb.toString();
	}

}
