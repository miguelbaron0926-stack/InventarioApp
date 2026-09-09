package InventarioApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Stock-Sync: Versión 1.0 (Demo Visual)
 * Interfaz profesional (Pestañas, Categorías), lógica simplificada para
 * presentación.
 */
public class MainApp extends Application {

    private Stage loginStage;
    private static final String TOTAL_COLOR = "#FF4500";
    private static final int TOTAL_FONT_SIZE = 24;

    // Rutas de imágenes portables
    private static final String IMG_DIR = "/InventarioApp/images/";
    private static final String LOGO = IMG_DIR + "logo.png";
    private static final String FONDO_LOGIN = IMG_DIR + "login_fondo.png";
    private static final String FONDO_ADMIN = IMG_DIR + "admin_fondo.png";
    private static final String FONDO_CLIENTE = IMG_DIR + "inventario_fondo.png";

    private Text totalPrefix, totalValue;
    private UsuarioService usuarioService = new UsuarioService();
    private InventarioService inventarioService = new InventarioService();
    private List<Producto> carrito = new ArrayList<>();

    // ================= MODELOS SIMPLIFICADOS =================
    public static class Usuario {
        String username, password, rol;

        public Usuario(String username, String password, String rol) {
            this.username = username;
            this.password = password;
            this.rol = rol;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRol() {
            return rol;
        }
    }

    public static class Producto {
        String id, nombre, categoria;
        int cantidad;
        double precio;

        public Producto(String id, String nombre, String categoria, int cantidad, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.categoria = categoria;
            this.cantidad = cantidad;
            this.precio = precio;
        }

        public String getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCategoria() {
            return categoria;
        }

        public int getCantidad() {
            return cantidad;
        }

        public double getPrecio() {
            return precio;
        }
    }

    public static class Proveedor {
        String nombre, contacto;

        public Proveedor(String nombre, String contacto) {
            this.nombre = nombre;
            this.contacto = contacto;
        }

        @Override
        public String toString() {
            return nombre + " - " + contacto;
        }
    }

    // ================= SERVICIOS BÁSICOS =================
    public static class UsuarioService {
        private List<Usuario> usuarios = new ArrayList<>();
        private static final String ARCH_USUARIOS = "usuarios.txt";

        public UsuarioService() {
            cargar();
            if (usuarios.isEmpty()) {
                usuarios.add(new Usuario("admin", "admin", "admin"));
                usuarios.add(new Usuario("cliente", "cliente", "cliente"));
            }
        }

        private void cargar() {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCH_USUARIOS))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if (datos.length == 3) {
                        usuarios.add(new Usuario(datos[0].trim(), datos[1].trim(), datos[2].trim()));
                    }
                }
            } catch (IOException e) {
                // Se usan las cuentas predeterminadas si no se encuentra el archivo.
            }
        }

        public Usuario validar(String user, String pass) {
            for (Usuario u : usuarios)
                if (u.getUsername().equals(user.trim()) && u.getPassword().equals(pass.trim()))
                    return u;
            return null;
        }
    }

    public static class InventarioService {
        private List<Producto> inventario = new ArrayList<>();
        private static final String ARCH_INV = "inventario.txt";

        public InventarioService() {
            cargar();
        }

        private void cargar() {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCH_INV))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] d = linea.split(",");
                    if (d.length == 5) {
                        inventario
                                .add(new Producto(d[0], d[1], d[2], Integer.parseInt(d[3]), Double.parseDouble(d[4])));
                    }
                }
            } catch (IOException e) {
                cargarPorDefecto(); // Si no existe, crea datos de prueba
            }
        }

        private void guardar() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCH_INV))) {
                for (Producto p : inventario) {
                    bw.write(p.id + "," + p.nombre + "," + p.categoria + "," + p.cantidad + "," + p.precio);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cargarPorDefecto() {
            inventario.add(new Producto("1", "Procesador Intel Core i5", "Procesadores", 12, 1100000));
            inventario.add(new Producto("2", "Tarjeta Gráfica RTX 4060", "Tarjetas Gráficas", 15, 1600000));
            inventario.add(new Producto("3", "Memoria RAM DDR5 16GB", "Memorias RAM", 20, 320000));
            inventario.add(new Producto("4", "SSD SATA 1TB", "Discos Duros", 25, 280000));
            inventario.add(new Producto("5", "Teclado Mecánico RGB", "Periféricos", 30, 220000));
            guardar();
        }

        public List<Producto> obtenerInventario() {
            return inventario;
        }

        public void agregarProducto(Producto p) {
            inventario.add(p);
            guardar();
        }

        public void eliminarProducto(String id) {
            inventario.removeIf(x -> x.id.equals(id));
            guardar();
        }

        public List<String> obtenerCategorias() {
            Set<String> cats = new LinkedHashSet<>();
            for (Producto p : inventario)
                cats.add(p.categoria);
            List<String> lista = new ArrayList<>();
            lista.add("Todas");
            lista.addAll(cats);
            return lista;
        }

        public List<Producto> filtrar(String texto, String categoria) {
            String txt = texto.toLowerCase().trim();
            return inventario.stream()
                    .filter(p -> ("Todas".equals(categoria) || p.categoria.equals(categoria))
                            && (p.id.toLowerCase().contains(txt) || p.nombre.toLowerCase().contains(txt)))
                    .collect(Collectors.toList());
        }

        public boolean descontarStock(String id, int cantidad) {
            for (Producto p : inventario) {
                if (p.id.equals(id)) {
                    if (p.cantidad < cantidad)
                        return false;
                    p.cantidad -= cantidad;
                    guardar();
                    return true;
                }
            }
            return false;
        }
    }

    // ================= UTILIDADES VISUALES =================
    private Image cargarImagen(String ruta) {
        try (InputStream is = getClass().getResourceAsStream(ruta)) {
            return is == null ? null : new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    private void configurarIcono(Stage stage) {
        Image icono = cargarImagen(LOGO);
        if (icono != null)
            stage.getIcons().add(icono);
    }

    private void aplicarFondo(Region root, String ruta) {
        Image img = cargarImagen(ruta);
        if (img == null) {
            root.setStyle("-fx-background-color: #f0f0f0;");
            return;
        }
        root.setBackground(
                new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
    }

    private StackPane crearFondo(Node contenido, String ruta) {
        Image img = cargarImagen(ruta);
        if (img == null)
            return new StackPane(contenido);
        ImageView fondo = new ImageView(img);
        fondo.setPreserveRatio(false);
        fondo.setManaged(false);
        fondo.setMouseTransparent(true);
        StackPane stack = new StackPane(fondo, contenido);
        stack.widthProperty().addListener((o, ov, nv) -> fondo.setFitWidth(nv.doubleValue()));
        stack.heightProperty().addListener((o, ov, nv) -> fondo.setFitHeight(nv.doubleValue()));
        return stack;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo);
        a.setContentText(msg);
        a.showAndWait();
    }

    private DecimalFormat df() {
        return new DecimalFormat("#,###");
    }

    // ================= FLUJO DE LA APP =================
    @Override
    public void start(Stage primaryStage) {
        this.loginStage = primaryStage;
        mostrarLogin();
    }

    private void mostrarLogin() {
        Stage s = loginStage;
        s.setTitle("Stock-Sync - Inicio de Sesión");
        configurarIcono(s);
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        aplicarFondo(root, FONDO_LOGIN);

        Label titulo = new Label("Stock-Sync");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web("#0F2C54"));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Usuario (admin / cliente)");
        txtUser.setMaxWidth(280);
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Contraseña");
        txtPass.setMaxWidth(280);

        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setStyle(
                "-fx-background-color:#0F2C54; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10 30; -fx-background-radius: 5;");

        btnLogin.setOnAction(e -> {
            Usuario u = usuarioService.validar(txtUser.getText(), txtPass.getText());
            if (u == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario o contraseña inválidos.");
            } else if ("admin".equals(u.getRol())) {
                s.hide();
                mostrarAdmin();
            } else {
                s.hide();
                mostrarCliente();
            }
        });

        root.getChildren().addAll(titulo, txtUser, txtPass, btnLogin);
        s.setScene(new Scene(crearFondo(root, FONDO_LOGIN), 400, 400));
        s.show();
    }

    // ================= CLIENTE (Visualmente completo, lógica simple)
    // =================
    private void mostrarCliente() {
        Stage s = new Stage();
        s.setTitle("Catálogo de Productos");
        configurarIcono(s);
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        aplicarFondo(root, FONDO_CLIENTE);

        Label t = new Label("Catálogo de Productos");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll(inventarioService.obtenerCategorias());
        cbCat.setValue("Todas");
        TextField txt = new TextField();
        txt.setPromptText("Buscar producto...");
        HBox filtros = new HBox(10, cbCat, txt);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        String bold = "-fx-font-weight:bold;";
        grid.add(new Label("ID") {
            {
                setStyle(bold);
            }
        }, 0, 0);
        grid.add(new Label("Nombre") {
            {
                setStyle(bold);
            }
        }, 1, 0);
        grid.add(new Label("Categoría") {
            {
                setStyle(bold);
            }
        }, 2, 0);
        grid.add(new Label("Stock") {
            {
                setStyle(bold);
            }
        }, 3, 0);
        grid.add(new Label("Cant. a Comprar") {
            {
                setStyle(bold);
            }
        }, 4, 0);
        grid.add(new Label("Precio") {
            {
                setStyle(bold);
            }
        }, 5, 0);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);

        totalPrefix = new Text("Total: $");
        totalPrefix.setFill(Color.web(TOTAL_COLOR));
        totalPrefix.setFont(Font.font("Arial", FontWeight.BOLD, TOTAL_FONT_SIZE));
        totalValue = new Text("0 COP");
        totalValue.setFill(Color.BLACK);
        totalValue.setFont(Font.font("Arial", FontWeight.BOLD, TOTAL_FONT_SIZE));

        Button btnPay = new Button("Procesar Pago");
        btnPay.setStyle(
                "-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10 30; -fx-background-radius: 5;");
        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.setStyle(
                "-fx-background-color:#dc3545; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10 20; -fx-background-radius: 5;");

        HBox bottom = new HBox(20, new TextFlow(totalPrefix, totalValue), btnPay, btnLogout);
        bottom.setAlignment(Pos.CENTER_LEFT);

        Runnable render = () -> {
            grid.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
            List<Producto> lista = inventarioService.filtrar(txt.getText(), cbCat.getValue());
            int r = 1;
            for (Producto p : lista) {
                grid.add(new Label(p.id), 0, r);
                grid.add(new Label(p.nombre), 1, r);
                grid.add(new Label(p.categoria), 2, r);
                grid.add(new Label(String.valueOf(p.cantidad)), 3, r);

                TextField tf = new TextField();
                tf.setPromptText("0");
                tf.setPrefWidth(60);
                tf.textProperty().addListener((obs, ov, nv) -> {
                    carrito.removeIf(x -> x.id.equals(p.id));
                    try {
                        int c = nv.isEmpty() ? 0 : Integer.parseInt(nv);
                        if (c > 0)
                            carrito.add(new Producto(p.id, p.nombre, p.categoria, c, c * p.precio));
                    } catch (NumberFormatException ex) {
                    }
                    actualizarTotal();
                });
                grid.add(tf, 4, r);
                grid.add(new Label(df().format(p.precio) + " COP"), 5, r);
                r++;
            }
        };

        cbCat.setOnAction(e -> render.run());
        txt.textProperty().addListener((o, ov, nv) -> render.run());
        render.run();

        // Lógica de pago SIMPLIFICADA para demo
        btnPay.setOnAction(e -> {
            if (carrito.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "El carrito está vacío.");
                return;
            }

            boolean exito = true;
            for (Producto c : carrito) {
                if (!inventarioService.descontarStock(c.id, c.cantidad)) {
                    exito = false;
                    break;
                }
            }

            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "¡Pago procesado con éxito! Inventario actualizado.");
                carrito.clear();
                actualizarTotal();
                render.run(); // Refresca la tabla
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No hay suficiente stock para completar la compra.");
                carrito.clear();
                actualizarTotal();
                render.run();
            }
        });

        btnLogout.setOnAction(e -> {
            s.close();
            loginStage.show();
        });

        root.getChildren().addAll(t, filtros, scroll, bottom);
        s.setScene(new Scene(crearFondo(root, FONDO_CLIENTE), 850, 600));
        s.show();
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = carrito.stream().mapToDouble(Producto::getPrecio).sum();
        totalValue.setText(df().format(total) + " COP");
    }

    // ================= ADMIN (Con Pestañas, pero lógica directa) =================
    private void mostrarAdmin() {
        Stage s = new Stage();
        s.setTitle("Panel Administrador");
        configurarIcono(s);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        aplicarFondo(root, FONDO_ADMIN);

        Label t = new Label("Panel de Administrador");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        t.setTextFill(Color.web("#0F2C54"));
        root.setTop(t);

        TabPane tabs = new TabPane();

        // PESTAÑA 1: VER INVENTARIO
        Tab tabVer = new Tab("Ver Inventario");
        tabVer.setClosable(false);
        VBox boxVer = new VBox(10);
        boxVer.setPadding(new Insets(10));
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar...");
        GridPane gridVer = new GridPane();
        gridVer.setHgap(15);
        gridVer.setVgap(8);
        String bold = "-fx-font-weight:bold;";
        gridVer.add(new Label("ID") {
            {
                setStyle(bold);
            }
        }, 0, 0);
        gridVer.add(new Label("Nombre") {
            {
                setStyle(bold);
            }
        }, 1, 0);
        gridVer.add(new Label("Categoría") {
            {
                setStyle(bold);
            }
        }, 2, 0);
        gridVer.add(new Label("Stock") {
            {
                setStyle(bold);
            }
        }, 3, 0);
        gridVer.add(new Label("Precio") {
            {
                setStyle(bold);
            }
        }, 4, 0);

        ScrollPane scrollVer = new ScrollPane(gridVer);
        scrollVer.setFitToWidth(true);
        scrollVer.setPrefHeight(400);

        Runnable actualizarTabla = () -> {
            gridVer.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
            int r = 1;
            for (Producto p : inventarioService.filtrar(txtBuscar.getText(), "Todas")) {
                gridVer.add(new Label(p.id), 0, r);
                gridVer.add(new Label(p.nombre), 1, r);
                gridVer.add(new Label(p.categoria), 2, r);
                gridVer.add(new Label(String.valueOf(p.cantidad)), 3, r);
                gridVer.add(new Label(df().format(p.precio) + " COP"), 4, r);
                r++;
            }
        };
        txtBuscar.textProperty().addListener((o, ov, nv) -> actualizarTabla.run());
        actualizarTabla.run();
        boxVer.getChildren().addAll(txtBuscar, scrollVer);
        tabVer.setContent(boxVer);

        // PESTAÑA 2: AGREGAR (Formulario simple y directo)
        Tab tabAdd = new Tab("Agregar Producto");
        tabAdd.setClosable(false);
        VBox boxAdd = new VBox(15);
        boxAdd.setPadding(new Insets(20));
        boxAdd.setMaxWidth(400);

        TextField addId = new TextField();
        addId.setPromptText("ID Único");
        TextField addNom = new TextField();
        addNom.setPromptText("Nombre del Producto");
        TextField addCat = new TextField();
        addCat.setPromptText("Categoría (ej: Periféricos)");
        TextField addCant = new TextField();
        addCant.setPromptText("Cantidad");
        TextField addPre = new TextField();
        addPre.setPromptText("Precio");

        Button btnAdd = new Button("Guardar Producto");
        btnAdd.setStyle(
                "-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10; -fx-background-radius: 5;");

        btnAdd.setOnAction(e -> {
            try {
                inventarioService.agregarProducto(new Producto(addId.getText(), addNom.getText(), addCat.getText(),
                        Integer.parseInt(addCant.getText()), Double.parseDouble(addPre.getText())));
                mostrarAlerta(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
                addId.clear();
                addNom.clear();
                addCat.clear();
                addCant.clear();
                addPre.clear();
                actualizarTabla.run(); // Actualiza la otra pestaña
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Verifica que todos los campos sean correctos.");
            }
        });
        boxAdd.getChildren().addAll(addId, addNom, addCat, addCant, addPre, btnAdd);
        tabAdd.setContent(boxAdd);

        // PESTAÑA 3: ELIMINAR (Formulario simple y directo)
        Tab tabDel = new Tab("Eliminar Producto");
        tabDel.setClosable(false);
        VBox boxDel = new VBox(15);
        boxDel.setPadding(new Insets(20));
        boxDel.setMaxWidth(400);

        TextField delId = new TextField();
        delId.setPromptText("ID del producto a eliminar");
        Button btnDel = new Button("Eliminar");
        btnDel.setStyle(
                "-fx-background-color:#dc3545; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10; -fx-background-radius: 5;");

        btnDel.setOnAction(e -> {
            if (delId.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Ingresa un ID.");
                return;
            }
            inventarioService.eliminarProducto(delId.getText());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Producto eliminado.");
            delId.clear();
            actualizarTabla.run();
        });
        boxDel.getChildren().addAll(delId, btnDel);
        tabDel.setContent(boxDel);

        tabs.getTabs().addAll(tabVer, tabAdd, tabDel);
        root.setCenter(tabs);

        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.setStyle(
                "-fx-background-color:#6c757d; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 20; -fx-background-radius: 5;");
        btnLogout.setOnAction(e -> {
            s.close();
            loginStage.show();
        });

        HBox bottom = new HBox(btnLogout);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(bottom);

        s.setScene(new Scene(crearFondo(root, FONDO_ADMIN), 800, 600));
        s.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}