package com.example.phone_book.vaadin;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import com.example.phone_book.entity.PhoneRecord;
import com.example.phone_book.repository.RecordRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

    private final RecordRepository repository;
    private final RecordEditor editor;

    private final Button addNewBtn = new Button("Запись", VaadinIcon.PLUS.create());
    private final Button createPDFBtn = new Button("Выгрузить в PDF", VaadinIcon.PACKAGE.create());
    final Grid<PhoneRecord> grid = new Grid<>();

    public MainView(RecordRepository recordRepository, RecordEditor recordEditor) {
        this.repository = recordRepository;
        this.editor = recordEditor;
        initHeader();
        initMainGrid(repository);
        add(recordEditor);

        addNewBtn.addClickListener(e -> recordEditor.editRec(new PhoneRecord()));
        createPDFBtn.addClickListener(e -> {
            try {
                createPdf();
            } catch (IOException | DocumentException ex) {
                throw new RuntimeException(ex);
            }
        });
        recordEditor.setChangeHandler(() -> {
            recordEditor.setVisible(false);
            grid.setItems(repository.findAll());
        });
        grid.setItems(recordRepository.findAll());
    }

    private void initHeader() {
        VerticalLayout header = new VerticalLayout();
        header.add(new NativeLabel("Телефонная книга"), new HorizontalLayout(addNewBtn, createPDFBtn));
        add(header);
    }

    private void initMainGrid(RecordRepository recordRepository) {
        grid.setItems(recordRepository.findAll());

        grid.addColumn(PhoneRecord::getFirstName).setHeader("Имя");
        grid.addColumn(PhoneRecord::getLastName).setHeader("Фамилия");
        grid.addColumn(PhoneRecord::getPhone).setHeader("Телефон");
        grid.addColumn(PhoneRecord::getTelegramId).setHeader("Телеграм");
        grid.asSingleSelect().addValueChangeListener(e -> editor.editRec(e.getValue()));
        add(grid);
    }

    private void createPdf() throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("phone-book.pdf"));

        document.open();
        PdfPTable pdfPTable = new PdfPTable(4);

        BaseFont baseFont = BaseFont.createFont("/Times New Roman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont);
        Stream.of("Имя", "Фамилия", "Номер", "Телеграм").forEach(column -> {
                    PdfPCell header = new PdfPHeaderCell();
                    header.setBackgroundColor(BaseColor.CYAN);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(column, font));
                    pdfPTable.addCell(header);
                }

        );
        repository.findAll().forEach(phoneRecord -> {
            pdfPTable.addCell(new Phrase(phoneRecord.getFirstName(), font));
            pdfPTable.addCell(new Phrase(phoneRecord.getLastName(), font));
            pdfPTable.addCell(new Phrase(phoneRecord.getPhone(), font));
            pdfPTable.addCell(new Phrase(phoneRecord.getTelegramId(), font));
        });
        document.add(pdfPTable);
        document.close();
    }


}
