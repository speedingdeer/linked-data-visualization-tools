/**
 * Copyright (c) 2012 Ontology Engineering Group, Departamento de Inteligencia
 * Artificial, Facultad de Informetica, Universidad Politecnica de Madrid, Spain
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view;

import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.ShapeFilesPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Jonathan Gonzalez (jonathan@jonbaraq.eu)
 */
public class ShapeFilesView extends Composite implements ShapeFilesPresenter.Display {

    private final BrowserMessages messages;
    private final BrowserResources resources;
    final FormPanel formUpload = new FormPanel();
    private FlowPanel panel;
    private FileUpload fileUpload;
    private Button submitUrlButton;
    private Button submitUploadButton;

    @Inject
    public ShapeFilesView(BrowserMessages messages, BrowserResources resources) {
        this.resources = resources;
        this.messages = messages;
        initWidget(createFormUploadUi());
    }


    /*
     * ------------- Display API --
     */
    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void startProcessing() {
        // TODO Auto-generated method stub
    }

    @Override
    public void stopProcessing() {
        // TODO Auto-generated method stub
    }

    /*
     * ---------------- helper methods --
     */
    private Widget createFormUploadUi() {
        // Servlet action.
        formUpload.setAction("./upload");
        
        // FileUpload widget needs to be a POST method.
        formUpload.setMethod(FormPanel.METHOD_POST);
        panel = new FlowPanel();
        formUpload.setWidget(panel);
        
        // Add the section to put the URL to download shapefiles.
        panel.add(new Label(messages.urlToShapeFiles()));
        TextBox urlShapeFile = new TextBox();
        urlShapeFile.setName("urlShapeFile");
        panel.add(urlShapeFile);
        submitUrlButton = new Button(messages.submitAndDisplay());
        panel.add(submitUrlButton);
        
        panel.add(new Label(messages.blankLine()));
        panel.add(new Label(messages.zipFileMessage()));
        fileUpload = new FileUpload();
        fileUpload.setName("uploadFormElement");
        panel.add(fileUpload);
        submitUploadButton = new Button(messages.uploadAndDisplay());
        panel.add(submitUploadButton);

        return formUpload;
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public Button getSubmitUploadButton() {
        return this.submitUploadButton;
    }
    
    @Override
    public Button getSubmitUrlButton() {
        return this.submitUrlButton;
    }

    @Override
    public FormPanel getFormUpload() {
        return this.formUpload;
    }
    
}