package com.vojj.app.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@PageTitle("Home")
@AnonymousAllowed
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("Hello, World!"));
        add(new Paragraph("Vaadin + Spring Boot + MySQL is running."));
    }
}
