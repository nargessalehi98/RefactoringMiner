package gr.uom.java.xmi.diff;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import gr.uom.java.xmi.UMLAnnotation;

public class UMLAnnotationListDiff {
	private final List<UMLAnnotation> removedAnnotations;
	private final List<UMLAnnotation> addedAnnotations;
	private final List<UMLAnnotationDiff> annotationDiffList;

	public UMLAnnotationListDiff(List<UMLAnnotation> annotations1, List<UMLAnnotation> annotations2) {
		this.removedAnnotations = new ArrayList<>();
		this.addedAnnotations = new ArrayList<>();
		this.annotationDiffList = new ArrayList<>();
		List<SimpleEntry<UMLAnnotation, UMLAnnotation>> matchedAnnotations = new ArrayList<>();
		for(UMLAnnotation annotation1 : annotations1) {
			boolean found = false;
			for(UMLAnnotation annotation2 : annotations2) {
				if(annotation1.equals(annotation2)) {
					matchedAnnotations.add(new SimpleEntry<>(annotation1, annotation2));
					found = true;
					break;
				}
			}
			if(!found) {
				for(UMLAnnotation annotation2 : annotations2) {
					if(annotation1.getTypeName().equals(annotation2.getTypeName())) {
						matchedAnnotations.add(new SimpleEntry<>(annotation1, annotation2));
						found = true;
						break;
					}
				}
			}
			if(!found) {
				removedAnnotations.add(annotation1);
			}
		}
		for(UMLAnnotation annotation2 : annotations2) {
			boolean found = false;
			for(UMLAnnotation annotation1 : annotations1) {
				if(annotation1.equals(annotation2)) {
					matchedAnnotations.add(new SimpleEntry<>(annotation1, annotation2));
					found = true;
					break;
				}
			}
			if(!found) {
				for(UMLAnnotation annotation1 : annotations1) {
					if(annotation1.getTypeName().equals(annotation2.getTypeName())) {
						matchedAnnotations.add(new SimpleEntry<>(annotation1, annotation2));
						found = true;
						break;
					}
				}
			}
			if(!found) {
				addedAnnotations.add(annotation2);
			}
		}
		for(SimpleEntry<UMLAnnotation, UMLAnnotation> entry : matchedAnnotations) {
			UMLAnnotationDiff annotationDiff = new UMLAnnotationDiff(entry.getKey(), entry.getValue());
			if(!annotationDiff.isEmpty()) {
				annotationDiffList.add(annotationDiff);
			}
		}
	}

	public List<UMLAnnotation> getRemovedAnnotations() {
		return removedAnnotations;
	}

	public List<UMLAnnotation> getAddedAnnotations() {
		return addedAnnotations;
	}

	public List<UMLAnnotationDiff> getAnnotationDiffList() {
		return annotationDiffList;
	}

	public boolean isEmpty() {
		return removedAnnotations.isEmpty() && addedAnnotations.isEmpty() && annotationDiffList.isEmpty();
	}
}
