/*******************************************************************************
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * generated by Xtext
 */
package org.jnario.feature.ui.contentassist;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.emf.ecore.util.EcoreUtil.resolve;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.common.types.xtext.ui.TypeMatchFilters;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.xbase.XbaseQualifiedNameConverter;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotationsPackage;
import org.eclipse.xtext.xbase.conversion.XbaseQualifiedNameValueConverter;
import org.eclipse.xtext.xbase.imports.RewritableImportSection;
import org.eclipse.xtext.xbase.ui.contentassist.ImportingTypesProposalProvider;
import org.eclipse.xtext.xbase.ui.imports.ReplaceConverter;
import org.jnario.feature.feature.Feature;
import org.jnario.feature.feature.FeaturePackage;
import org.jnario.feature.feature.StepReference;
import org.jnario.feature.naming.StepNameProvider;

import com.google.common.base.Strings;
import com.google.inject.Inject;

/**
 * @author Birgit Engelmann - Initial contribution and API
 */
public class FeatureProposalProvider extends AbstractFeatureProposalProvider {
	
	@Inject private IResourceDescriptions resourceDescriptions;
	@Inject private IContainer.Manager containerManager;
	@Inject private StepNameProvider stepNameProvider;
	@Inject private RewritableImportSection.Factory importSectionFactory;
	@Inject	private ReplaceConverter replaceConverter;
	
	@Override
	public void completeXAnnotation_AnnotationType(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeJavaTypes(context, XAnnotationsPackage.Literals.XANNOTATION__ANNOTATION_TYPE, 
				TypeMatchFilters.all(IJavaSearchConstants.ANNOTATION_TYPE), acceptor);
	}
	
//	@Override
//	public void completeMockLiteral_Type(EObject model, Assignment assignment,
//			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		completeJavaTypes(context, XbasePackage.Literals.XTYPE_LITERAL__TYPE, 
//				TypeMatchFilters.all(IJavaSearchConstants.CLASS_AND_INTERFACE), acceptor);
//	}
	
	@Override
	public void completeAndReference_Reference(EObject model,
			Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		acceptor = createStepShortener(model, context, acceptor, "And");
		acceptor = createStepShortener(model, context, acceptor, "But");
		super.completeAndReference_Reference(model, assignment, context, acceptor);
	}
	
	@Override
	public void completeGivenReference_Reference(EObject model,
			Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		acceptor = createStepShortener(model, context, acceptor, "Given");
		super.completeGivenReference_Reference(model, assignment, context, acceptor);
	}
	
	@Override
	public void completeWhenReference_Reference(EObject model,
			Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		acceptor = createStepShortener(model, context, acceptor, "When");
		super.completeWhenReference_Reference(model, assignment, context, acceptor);
	}
	
	@Override
	public void completeThenReference_Reference(EObject model,
			Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		acceptor = createStepShortener(model, context, acceptor, "Then");
		super.completeThenReference_Reference(model, assignment, context, acceptor);
	}

	private void completeStepReference(EObject model,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor,
			String stepPrefix) {
		IScope scope = createStepScope(model);
		for (IEObjectDescription desc : scope.getAllElements()) {
			StepReference ref = (StepReference) resolve(desc.getEObjectOrProxy(), model);
			createProposal(context, acceptor, stepPrefix, ref);
		}
	}

	protected IScope createStepScope(EObject model) {
		List<IEObjectDescription> scopedElements = newArrayList();
		for (IContainer container : visibleContainers(model)) {
			Iterable<IEObjectDescription> descs = container.getExportedObjectsByType(FeaturePackage.Literals.STEP_REFERENCE);
			addAll(scopedElements, descs);
		}
		IScope scope = new SimpleScope(scopedElements);
		return scope;
	}

	public void createProposal(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor, String stepPrefix,
			StepReference ref) {
		String name = stepNameProvider.nameOf(ref);
		name = stepNameProvider.removeKeywords(name);
		name = stepNameProvider.removeArguments(name);
		if(Strings.isNullOrEmpty(name)){
			return;
		}
		String proposal = stepPrefix + " " + name;
		acceptor.accept(createCompletionProposal(proposal, name, getLabelProvider().getImage(ref) , context));
	}

	public List<IContainer> visibleContainers(EObject model) {
		IResourceDescription.Manager resourceDescManager = ((XtextResource)model.eResource()).getResourceServiceProvider().getResourceDescriptionManager();
		IResourceDescription resourceDescription = resourceDescManager.getResourceDescription(model.eResource());
		List<IContainer> visibleContainers = containerManager.getVisibleContainers(resourceDescription, resourceDescriptions);
		return visibleContainers;
	}

	@Override
	public void completeKeyword(Keyword keyword,
			ContentAssistContext contentAssistContext,
			ICompletionProposalAcceptor acceptor) {
		if("import".equals(keyword.getValue()) && contentAssistContext.getPreviousModel() instanceof Feature){
			return;
		}
		super.completeKeyword(keyword, contentAssistContext, acceptor);
	}
	
	@Override
	public void complete_FEATURE_TEXT(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "Feature: ";
		if (context.getPreviousModel() instanceof Feature) {
			complete_BACKGROUND_TEXT(model, ruleCall, context, acceptor);
			complete_SCENARIO_TEXT(model, ruleCall, context, acceptor);
		}else{
			acceptor.accept(createCompletionProposal(proposal, proposal, getLabelProvider().getImage(model), context));
		}
	}
	
	@Override
	public void complete_BACKGROUND_TEXT(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "Background: ";
		acceptor.accept(createCompletionProposal(proposal, proposal, getLabelProvider().getImage(model), context));
	}
	
	
	@Override
	public void complete_SCENARIO_TEXT(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "Scenario: ";
		acceptor.accept(createCompletionProposal(proposal, proposal, getLabelProvider().getImage(model), context));
	}
	
	@Override
	public void complete_GIVEN_TEXT(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "Given ";
		acceptor.accept(createCompletionProposal(proposal, proposal, getLabelProvider().getImage(model), context));
	}
	
	@Override
	public void complete_WHEN_TEXT(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "When ";
		acceptor.accept(createCompletionProposal(proposal, proposal, getLabelProvider().getImage(model), context));
	}
	
	@Override
	public void complete_THEN_TEXT(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "Then ";
		acceptor.accept(createCompletionProposal(proposal, proposal, getLabelProvider().getImage(model), context));
	}

	protected ICompletionProposalAcceptor createStepShortener(EObject model,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor, String prefix) {
		IScope scope = getScopeProvider().getScope(model, FeaturePackage.Literals.STEP_REFERENCE__REFERENCE);
		acceptor = createStepFqnShorterner(context, acceptor, scope, prefix);
		completeStepReference(model, context, acceptor, prefix);
		return acceptor;
	}
	
	@Override
	protected void lookupCrossReference(CrossReference crossReference, ContentAssistContext contentAssistContext,
			ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(crossReference, contentAssistContext, acceptor, getFeatureDescriptionPredicate(contentAssistContext));
	}
	
	protected StyledString getStyledDisplayString(EObject element, String qualifiedName, String shortName) {
		return new StyledString(getDisplayString(element, qualifiedName, shortName));
	}
	
	public ICompletionProposalAcceptor createStepFqnShorterner(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor, IScope scope, final String prefix) {
		
		XbaseQualifiedNameValueConverter qualifiedNameValueConverter = new XbaseQualifiedNameValueConverter(){
			@Override
			public String toString(String value) {
				value = value.substring(prefix.length() + 1);
				int end = value.lastIndexOf('.');
				String result = value.substring(0, end) + ".*";
				return result;
			}
		};
		final IQualifiedNameConverter qualifiedNameConverter = new XbaseQualifiedNameConverter(){
			@Override
			public QualifiedName toQualifiedName(String qualifiedNameAsString) {
				return new QualifiedName(qualifiedNameAsString.split("\\.")){
					public String getLastSegment() {
						return prefix + " " + super.getLastSegment();
					};
				};
			}
		};
		
		final ImportingTypesProposalProvider.FQNImporter fqnImporter = new ImportingTypesProposalProvider.FQNImporter(context.getResource(), context.getViewer(), scope, qualifiedNameConverter,
				qualifiedNameValueConverter, importSectionFactory, replaceConverter);
		
		final ICompletionProposalAcceptor scopeAware = new ICompletionProposalAcceptor.Delegate(acceptor) {
			@Override
			public void accept(ICompletionProposal proposal) {
				if (proposal instanceof ConfigurableCompletionProposal) {
					ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) proposal;
					configurableCompletionProposal.setTextApplier(fqnImporter);
				}
				super.accept(proposal);
			}
		};
		return scopeAware;
	}
	
}
