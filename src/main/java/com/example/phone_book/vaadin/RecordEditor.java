package com.example.phone_book.vaadin;

import com.example.phone_book.entity.PhoneRecord;
import com.example.phone_book.repository.RecordRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class RecordEditor extends VerticalLayout implements KeyNotifier {

    private final RecordRepository recordRepository;
    private PhoneRecord phoneRecord;

    Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    Button cancel = new Button("Отмена");
    Button delete = new Button("Удалить", VaadinIcon.TRASH.create());

    TextField firstName = new TextField("Имя");
    TextField lastName = new TextField("Фамилия");
    EmailField email = new EmailField("Email");
    TextField phone = new TextField("Номер телефона");
    TextField telegramId = new TextField("Телеграм");

    Binder<PhoneRecord> binder = new Binder<>(PhoneRecord.class);

    private ChangeHandler changeHandler;

    public RecordEditor(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        initFields();
        initActions();
        setVisible(false);
    }

    void initFields() {
        HorizontalLayout fields = new HorizontalLayout(firstName, lastName, phone, email, telegramId);
        binder.forField(firstName).asRequired("Введите имя").bind(PhoneRecord::getFirstName,PhoneRecord::setFirstName);
        binder.forField(lastName).asRequired("Введите фамилию").bind(PhoneRecord::getLastName,PhoneRecord::setLastName);

        email.setRequiredIndicatorVisible(true);
        email.setPattern(
                "^[a-zA-Z0-9_\\-+]+(?:\\.[a-zA-Z0-9_\\-+]+)*@example\\.com$");
        email.setI18n(new EmailField.EmailFieldI18n()
                .setPatternErrorMessage(
                        "Не валидный адрес"));

        phone.setRequired(true);
        phone.setPattern(
                "^[+]?[\\(]?[0-9]{3}[\\)]?[\\-]?[0-9]{3}[\\-]?[0-9]{4,6}$");
        phone.setAllowedCharPattern("[0-9()+-]");
        phone.setMinLength(5);
        phone.setMaxLength(18);

        phone.setI18n(new TextField.TextFieldI18n()
                .setRequiredErrorMessage("Введите номер ")
                .setMinLengthErrorMessage("Минимум 5 знаков")
                .setMaxLengthErrorMessage("Максимум 18")
                .setPatternErrorMessage("Неверный формат номера"));
        phone.setRequiredIndicatorVisible(true);
        phone.setHelperText("Format: +1234567890");
        setSpacing(true);

        binder.forField(phone).asRequired("Введите номер телефона").bind(PhoneRecord::getPhone,PhoneRecord::setPhone);
        add(fields);
    }

    void initActions() {
        addKeyPressListener(Key.ENTER, e -> saveRecord());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> saveRecord());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(e -> deleteRecord());
        cancel.addClickListener(e -> setVisible(false));
        HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
        add(actions);
    }

    public interface ChangeHandler {
        void onChange();
    }

    void deleteRecord() {
        recordRepository.delete(phoneRecord);
        changeHandler.onChange();
    }

    void saveRecord() {
        if (binder.validate().isOk()) {
            recordRepository.save(phoneRecord);
            changeHandler.onChange();
        }
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }

    public final void editRec(PhoneRecord r) {
        if (r == null) {
            setVisible(false);
            return;
        }
        delete.setVisible(r.getId() != null);
        phoneRecord = recordRepository.findById(r.getId()).orElse(r);
        binder.setBean(phoneRecord);
        setVisible(true);
        firstName.focus();
    }
}
