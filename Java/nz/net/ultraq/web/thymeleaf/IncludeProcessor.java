
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.ATTRIBUTE_NAME_FRAGMENT_FULL;
import static nz.net.ultraq.web.thymeleaf.LayoutDialect.LAYOUT_PREFIX;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

import java.util.List;
import java.util.Map;

/**
 * Processor for the 'layout:include' attribute, does the same as Thymeleaf's
 * include tag but allows for the passing of element fragments to the included
 * page.
 * 
 * @author Emanuel Rabina
 */
public class IncludeProcessor extends AbstractProcessor {

	static final String ATTRIBUTE_NAME_INCLUDE = "include";
	static final String ATTRIBUTE_NAME_INCLUDE_FULL = LAYOUT_PREFIX + ":" + ATTRIBUTE_NAME_INCLUDE;

	/**
	 * Constructor, sets this processor to work on the 'include' attribute.
	 */
	public IncludeProcessor() {

		super(ATTRIBUTE_NAME_INCLUDE);
	}

	/**
	 * Locates the specified page and includes it into the current template.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the page and fragment to include
		FragmentAndTarget fragmentandtarget = StandardFragmentProcessor.computeStandardFragmentSpec(
				arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName),
				null, ATTRIBUTE_NAME_FRAGMENT_FULL);
		List<Node> includefragments = fragmentandtarget.extractFragment(arguments.getConfiguration(),
				arguments.getContext(), arguments.getTemplateRepository());

		// Gather all fragment parts from the scope of the include and store for later use
		Map<String,Object> pagefragments = findFragments(element.getElementChildren());

		// Place the include page fragment into this element and replace it
		if (includefragments != null && !includefragments.isEmpty()) {
			Element fragment = (Element)includefragments.get(0);
			element.clearChildren();
			for (Node node: fragment.getChildren()) {
				element.addChild(node.cloneNode(null, true));
			}
		}

		// Remove the include attribute
		element.removeAttribute(attributeName);

		// Scope the page fragments to this element
		if (!pagefragments.isEmpty()) {
			return ProcessorResult.setLocalVariables(pagefragments);
		}
		return ProcessorResult.OK;
	}
}
