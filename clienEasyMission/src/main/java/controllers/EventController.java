package controllers;

import java.awt.Insets;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import clienEasyMission.MainFx;
import entities.Events;
import entities.Notification;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.converter.LocalDateStringConverter;
import services.EventEJBRemote;
import services.NotificationServiceEJBRemote;
import services.UserEJBRemote;

public class EventController extends AnchorPane implements Initializable {

	@FXML
	private ImageView imgv;

	@FXML
	private JFXDrawer drawer;

	@FXML
	private VBox box;

	@FXML
	private JFXHamburger hamburger;

	@FXML
	private JFXButton idforum;
	@FXML
    private ImageView image;
	
	@FXML
	private TableView<Events> tableaffichage;
	@FXML
	private TableColumn<Events, String> colonetitre;

	@FXML
	private TableColumn<Events, String> coloneadress;

	@FXML
	private TableColumn<Events, String> colonedescription;

	@FXML
	private TableColumn<Events, Date> colonedebut;

	@FXML
	private TableColumn<Events, Date> colonefin;

	@FXML
	private JFXDatePicker datefin;

	@FXML
	private JFXDatePicker datedebut;
	@FXML
	private JFXTextField titre;
	@FXML
	private JFXTextField description;
	@FXML
	private JFXTextField adresse;
	@FXML
	private Label erreur;
	@FXML
	private Label number;
	
	MainFx application;

	public MainFx getApplication() {
		return application;
	}

	public void setApplication(MainFx application) {
		this.application = application;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			NotificationServiceEJBRemote proxy1 = (NotificationServiceEJBRemote) ctx.lookup(
					"/easyMission-ear/easyMission-ejb/NotificationServiceEJB!services.NotificationServiceEJBRemote");
			if (proxy1.getAllNotificationByUserBySeen(application.person).isEmpty()) {
				image.setVisible(false);

			} else number.setText(""+proxy1.countNotifNotSeen(application.person));
		 

		

		drawer.setSidePane(box);

		HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
		transition.setRate(-1);
		hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
			transition.setRate(transition.getRate() * -1);
			transition.play();

			if (drawer.isShown()) {

				drawer.close();
			} else {
				drawer.open();
			}
		});
		
			ctx = new InitialContext();
			EventEJBRemote proxy;

			proxy = (EventEJBRemote) ctx.lookup("/easyMission-ear/easyMission-ejb/EventEJB!services.EventEJBRemote");

			colonetitre.setCellValueFactory(new PropertyValueFactory<Events, String>("titre"));
			coloneadress.setCellValueFactory(new PropertyValueFactory<Events, String>("adresse"));
			colonedescription.setCellValueFactory(new PropertyValueFactory<Events, String>("description"));
			colonedebut.setCellValueFactory(new PropertyValueFactory<Events, Date>("datedebut"));
			colonefin.setCellValueFactory(new PropertyValueFactory<Events, Date>("datefin"));
			ObservableList<Events> data = FXCollections.observableArrayList(proxy.showall());
			tableaffichage.setItems(data);
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@FXML
	void forumm(ActionEvent event) {
		// application.gotoForum();
	}

	@FXML
	void ajouter(ActionEvent event) throws NamingException {

		InitialContext ctx;
		ctx = new InitialContext();
		EventEJBRemote proxy;
		proxy = (EventEJBRemote) ctx.lookup("/easyMission-ear/easyMission-ejb/EventEJB!services.EventEJBRemote");
		NotificationServiceEJBRemote proxy1 = (NotificationServiceEJBRemote) ctx.lookup(
				"/easyMission-ear/easyMission-ejb/NotificationServiceEJB!services.NotificationServiceEJBRemote");
		UserEJBRemote proxy2 = (UserEJBRemote) ctx
				.lookup("/easyMission-ear/easyMission-ejb/UserEJB!services.UserEJBRemote");

		if (!titre.getText().equals("") && !description.getText().equals("") && !adresse.getText().equals("")) {

			Events e = new Events(titre.getText(), description.getText(), adresse.getText());
			Date d;
			if (datedebut.getValue() == null) {
				d = new Date(0);
			} else {
				d = new Date(datedebut.getValue().getYear() - 1900, datedebut.getValue().getMonthValue() - 1,
						datedebut.getValue().getDayOfMonth());
			}
			Date d1;
			if (datefin.getValue() == null) {
				d1 = new Date(0);
			} else {
				d1 = new Date(datefin.getValue().getYear() - 1900, datefin.getValue().getMonthValue() - 1,
						datefin.getValue().getDayOfMonth());
			}

			e.setDatefin(d1);
			e.setDatedebut(d);
			proxy.AddEvent(e);
			for (User user : proxy2.getAllUser()) {
				Notification n = new Notification();
				n.setContent("There is a new Event : " + e.getTitre());
				n.setSendDate(new Date());
				n.setUser(user);
				n.setSeen(false);
				proxy1.addNotification(n);
			}

			titre.clear();
			description.clear();
			adresse.clear();
			ObservableList<Events> data = FXCollections.observableArrayList(proxy.showall());
			tableaffichage.setItems(data);
		} else {
			erreur.setText("champs vide!!");
		}
	}

	@FXML
	void delete(ActionEvent event) {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			EventEJBRemote proxy;
			proxy = (EventEJBRemote) ctx.lookup("/easyMission-ear/easyMission-ejb/EventEJB!services.EventEJBRemote");

			Integer selected = tableaffichage.getSelectionModel().getSelectedIndex();

			if (tableaffichage.getSelectionModel().isSelected(selected)) {
				proxy.DeleteEvent(tableaffichage.getItems().get(selected));
				tableaffichage.getItems().remove(tableaffichage.getItems().get(selected));

			} else {
				System.out.println("selectionner un �lement" + selected);
			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	void update(ActionEvent event) throws NamingException {
		Integer selected = tableaffichage.getSelectionModel().getSelectedIndex();
		InitialContext ctx;
		ctx = new InitialContext();
		EventEJBRemote proxy;
		proxy = (EventEJBRemote) ctx.lookup("/easyMission-ear/easyMission-ejb/EventEJB!services.EventEJBRemote");
		if (tableaffichage.getSelectionModel().isSelected(selected)) {

			Events ee = tableaffichage.getItems().get(selected);
			ee.setTitre(titre.getText());
			ee.setAdresse(adresse.getText());
			ee.setDescription(description.getText());
			Date d;
			if (datedebut.getValue() == null) {
				d = new Date(0);
			} else {
				d = new Date(datedebut.getValue().getYear() - 1900, datedebut.getValue().getMonthValue() - 1,
						datedebut.getValue().getDayOfMonth());
			}
			Date d1;
			if (datefin.getValue() == null) {
				d1 = new Date(0);
			} else {
				d1 = new Date(datefin.getValue().getYear() - 1900, datefin.getValue().getMonthValue() - 1,
						datefin.getValue().getDayOfMonth());
			}

			ee.setDatefin(d1);
			ee.setDatedebut(d);
			proxy.UpdateEvent(ee);

		}
		ObservableList<Events> data = FXCollections.observableArrayList(proxy.showall());
		tableaffichage.setItems(data);
	}

	private Calendar pickDateConversion(DatePicker datePicker) {
		Calendar cal = Calendar.getInstance();
		Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		cal.setTime(date);
		return cal;
	}

	@FXML
	void getev() throws ParseException {

		SimpleDateFormat simple = new SimpleDateFormat("dd/MM/yyyy");
		Integer selected = tableaffichage.getSelectionModel().getSelectedIndex();
		Events e = tableaffichage.getItems().get(selected);

		Date input = simple.parse(simple.format(e.getDatedebut()));
		Date input1 = simple.parse(simple.format(e.getDatefin()));

		Instant instant = input.toInstant();
		Instant instant1 = input1.toInstant();

		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		ZonedDateTime zdt1 = instant1.atZone(ZoneId.systemDefault());

		LocalDate date = zdt.toLocalDate();
		LocalDate date1 = zdt1.toLocalDate();

		datedebut.setValue(date);
		datefin.setValue(date1);

		titre.setText(e.getTitre());
		adresse.setText(e.getAdresse());
		description.setText(e.getDescription());
		// datedebut.setValue(e.getDatedebut());
		// datefin.setValue(e.getDatefin());
	}

	@FXML
	void Messages(ActionEvent event) {
		application.gotoMessage();
	}

	@FXML
	void LogOut(ActionEvent event) {
		application.gotoLogin();
	}

	@FXML
	void Notifications(ActionEvent event) {
		application.gotoNotification();
	}

}