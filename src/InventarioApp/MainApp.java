package InventarioApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
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
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage loginStage;
    private static final String TOTAL_COLOR = "#FF4500";
    private static final int TOTAL_FONT_SIZE = 24;

    // ================= RUTAS DE IMÁGENES (CLASSPATH, PORTABLES) =================
    private static final String IMG_DIR = "/InventarioApp/images/";
    private static final String LOGO = IMG_DIR + "logo.png";
    private static final String FONDO_LOGIN = IMG_DIR + "login_fondo.png";
    private static final String FONDO_ADMIN = IMG_DIR + "admin_fondo.png";
    private static final String FONDO_CLIENTE = IMG_DIR + "inventario_fondo.png";
    private static final String FONDO_GENERAL = IMG_DIR + "fondo.png";

    private Text totalPrefix, totalValue;
    private UsuarioService usuarioService = new UsuarioService();
    private InventarioService inventarioService = new InventarioService();
    private VentasService ventasService = new VentasService();
    private List<Producto> carrito = new ArrayList<>();

    // ================= MODELOS =================
    public static class Usuario {
        private String username, password, rol, nombres, apellidos, cedula, email;

        public Usuario(String username, String password, String rol) {
            this.username = username;
            this.password = password;
            this.rol = rol;
        }

        public Usuario(String username, String password, String rol, String nombres, String apellidos, String cedula,
                String email) {
            this(username, password, rol);
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.cedula = cedula;
            this.email = email;
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

        public String getEmail() {
            return email;
        }

        public String getNombres() {
            return nombres;
        }

        public String getApellidos() {
            return apellidos;
        }

        public String getCedula() {
            return cedula;
        }
    }

    public static class Producto {
        private String id, nombre, categoria;
        private int cantidad;
        private double precio;

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

        @Override
        public String toString() {
            return "ID: " + id + " | " + nombre + " [" + categoria + "] | Cant: " + cantidad;
        }
    }

    public static class Proveedor {
        private String nombre, contacto;

        public Proveedor(String nombre, String contacto) {
            this.nombre = nombre;
            this.contacto = contacto;
        }

        public String getNombre() {
            return nombre;
        }

        public String getContacto() {
            return contacto;
        }

        @Override
        public String toString() {
            return nombre + " – " + contacto;
        }
    }

    public static class Venta {
        String idVenta, fecha, detalleProductos, total;

        public Venta(String idVenta, String fecha, String detalleProductos, String total) {
            this.idVenta = idVenta;
            this.fecha = fecha;
            this.detalleProductos = detalleProductos;
            this.total = total;
        }

        public String getIdVenta() {
            return idVenta;
        }

        public String getFecha() {
            return fecha;
        }

        public String getDetalleProductos() {
            return detalleProductos;
        }

        public String getTotal() {
            return total;
        }
    }

    // ================= SERVICIOS =================
    public static class UsuarioService {
        private List<Usuario> usuarios = new ArrayList<>();
        private static final String ARCHIVO = "usuarios.txt";

        public UsuarioService() {
            cargar();
        }

        private void cargar() {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] d = linea.split(",");
                    if (d.length >= 7)
                        usuarios.add(new Usuario(d[0], d[1], d[2], d[3], d[4], d[5], d[6]));
                    else if (d.length >= 3)
                        usuarios.add(new Usuario(d[0], d[1], d[2]));
                }
            } catch (IOException e) {
                usuarios.add(new Usuario("admin", "admin", "admin", "Administrador", "Sistema", "0000000000",
                        "admin@stocksync.com"));
                usuarios.add(new Usuario("cliente", "cliente", "cliente", "Cliente", "Ejemplo", "1234567890",
                        "cliente@example.com"));
                guardar();
            }
        }

        private void guardar() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
                for (Usuario u : usuarios) {
                    bw.write(u.getUsername() + "," + u.getPassword() + "," + u.getRol() + ","
                            + (u.getNombres() != null ? u.getNombres() : "") + ","
                            + (u.getApellidos() != null ? u.getApellidos() : "") + ","
                            + (u.getCedula() != null ? u.getCedula() : "") + ","
                            + (u.getEmail() != null ? u.getEmail() : ""));
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Usuario validar(String user, String pass) {
            for (Usuario u : usuarios)
                if (u.getUsername().equalsIgnoreCase(user.trim()) && u.getPassword().equals(pass.trim()))
                    return u;
            return null;
        }

        public boolean existeUsuario(String user) {
            return usuarios.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user.trim()));
        }

        public void registrar(Usuario u) {
            if (!existeUsuario(u.getUsername())) {
                usuarios.add(u);
                guardar();
            }
        }

        public List<Usuario> obtenerUsuarios() {
            return usuarios;
        }

        public int importarCSV(File archivo) {
            List<Usuario> temp = new ArrayList<>();
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    try {
                        String[] d = linea.split(",");
                        if (d.length >= 7) {
                            temp.add(new Usuario(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(), d[4].trim(),
                                    d[5].trim(), d[6].trim()));
                            count++;
                        } else if (d.length >= 3) {
                            temp.add(new Usuario(d[0].trim(), d[1].trim(), d[2].trim()));
                            count++;
                        }
                    } catch (Exception e) {
                        /* Ignorar fila */ }
                }
                usuarios.clear();
                usuarios.addAll(temp);
                guardar();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

        public void exportarCSV(File archivo) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (Usuario u : usuarios) {
                    bw.write(u.getUsername() + "," + u.getPassword() + "," + u.getRol() + ","
                            + (u.getNombres() != null ? u.getNombres() : "") + ","
                            + (u.getApellidos() != null ? u.getApellidos() : "") + ","
                            + (u.getCedula() != null ? u.getCedula() : "") + ","
                            + (u.getEmail() != null ? u.getEmail() : ""));
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class InventarioService {
        private List<Producto> inventario = new ArrayList<>();
        private List<Proveedor> proveedores = new ArrayList<>();
        private static final String ARCH_INV = "inventario.txt";
        private static final String ARCH_PROV = "proveedores.txt";

        public InventarioService() {
            cargar();
        }

        private void cargar() {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCH_INV))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] d = linea.split(",");
                    if (d.length == 5)
                        inventario
                                .add(new Producto(d[0], d[1], d[2], Integer.parseInt(d[3]), Double.parseDouble(d[4])));
                }
            } catch (IOException e) {
                cargarPorDefecto();
            }
            try (BufferedReader br = new BufferedReader(new FileReader(ARCH_PROV))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] d = linea.split(",");
                    if (d.length == 2)
                        proveedores.add(new Proveedor(d[0], d[1]));
                }
            } catch (IOException e) {
                cargarProveedoresDefecto();
            }
        }

        private void guardarInventario() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCH_INV))) {
                for (Producto p : inventario) {
                    bw.write(p.getId() + "," + p.getNombre() + "," + p.getCategoria() + "," + p.getCantidad() + ","
                            + p.getPrecio());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void guardarProveedores() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCH_PROV))) {
                for (Proveedor p : proveedores) {
                    bw.write(p.getNombre() + "," + p.getContacto());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cargarPorDefecto() {
            inventario.add(new Producto("1", "Procesador Intel Core i5-13400", "Procesadores", 12, 1100000));
            inventario.add(new Producto("2", "Tarjeta Gráfica RTX 4060", "Tarjetas Gráficas", 15, 1600000));
            inventario.add(new Producto("3", "Memoria RAM DDR5 16GB", "Memorias RAM", 20, 320000));
            inventario.add(new Producto("4", "SSD SATA 1TB", "Discos Duros", 25, 280000));
            inventario.add(new Producto("5", "Teclado Mecánico RGB", "Periféricos", 30, 220000));
            guardarInventario();
        }

        private void cargarProveedoresDefecto() {
            proveedores.add(new Proveedor("NetZone", "contacto@netzone.com"));
            proveedores.add(new Proveedor("CoolTech", "ventas@cooltech.com"));
            guardarProveedores();
        }

        public List<Producto> obtenerInventario() {
            return inventario;
        }

        public List<Proveedor> obtenerProveedores() {
            return proveedores;
        }

        public void agregarProducto(Producto p) {
            inventario.add(p);
            guardarInventario();
        }

        public void eliminarProducto(String id) {
            inventario.removeIf(x -> x.getId().equals(id));
            guardarInventario();
        }

        public void actualizarProducto(Producto p) {
            for (Producto x : inventario) {
                if (x.getId().equals(p.getId())) {
                    x.cantidad = p.getCantidad();
                    x.precio = p.getPrecio();
                    x.categoria = p.getCategoria();
                    guardarInventario();
                    return;
                }
            }
        }

        public void agregarProveedor(Proveedor p) {
            proveedores.add(p);
            guardarProveedores();
        }

        public List<String> obtenerCategorias() {
            Set<String> cats = new LinkedHashSet<>();
            for (Producto p : inventario)
                cats.add(p.getCategoria());
            List<String> lista = new ArrayList<>();
            lista.add("Todas");
            lista.addAll(cats);
            return lista;
        }

        public List<Producto> filtrar(String texto, String categoria) {
            String txt = texto.toLowerCase().trim();
            return inventario.stream().filter(p -> ("Todas".equals(categoria) || p.getCategoria().equals(categoria))
                    && (p.getId().toLowerCase().contains(txt) || p.getNombre().toLowerCase().contains(txt)
                            || p.getCategoria().toLowerCase().contains(txt)))
                    .collect(Collectors.toList());
        }

        public int importarInventarioCSV(File archivo) {
            List<Producto> temp = new ArrayList<>();
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    try {
                        String[] d = linea.split(",");
                        if (d.length == 5) {
                            temp.add(new Producto(d[0].trim(), d[1].trim(), d[2].trim(), Integer.parseInt(d[3].trim()),
                                    Double.parseDouble(d[4].trim())));
                            count++;
                        }
                    } catch (Exception e) {
                        /* Ignorar fila */ }
                }
                inventario.clear();
                inventario.addAll(temp);
                guardarInventario();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

        public void exportarInventarioCSV(File archivo) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (Producto p : inventario) {
                    bw.write(p.getId() + "," + p.getNombre() + "," + p.getCategoria() + "," + p.getCantidad() + ","
                            + p.getPrecio());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int importarProveedoresCSV(File archivo) {
            List<Proveedor> temp = new ArrayList<>();
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    try {
                        String[] d = linea.split(",");
                        if (d.length == 2) {
                            temp.add(new Proveedor(d[0].trim(), d[1].trim()));
                            count++;
                        }
                    } catch (Exception e) {
                        /* Ignorar fila */ }
                }
                proveedores.clear();
                proveedores.addAll(temp);
                guardarProveedores();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

        public void exportarProveedoresCSV(File archivo) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (Proveedor p : proveedores) {
                    bw.write(p.getNombre() + "," + p.getContacto());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class VentasService {
        private List<Venta> ventas = new ArrayList<>();
        private static final String ARCH_VENTAS = "ventas.txt";

        public VentasService() {
            cargar();
        }

        private void cargar() {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCH_VENTAS))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] d = linea.split("\\|");
                    if (d.length == 4)
                        ventas.add(new Venta(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim()));
                }
            } catch (IOException e) {
                /* Si no existe, se crea al guardar la primera venta */ }
        }

        private void guardar() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCH_VENTAS))) {
                for (Venta v : ventas) {
                    bw.write(v.idVenta + " | " + v.fecha + " | " + v.detalleProductos + " | " + v.total);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void registrarVenta(List<Producto> carrito, double total) {
            String idVenta = "VTA-" + new Random().nextInt(90000) + 10000;
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            StringBuilder detalle = new StringBuilder();
            for (Producto p : carrito) {
                detalle.append(p.getNombre()).append(" (x").append(p.getCantidad()).append(") ");
            }
            DecimalFormat df = new DecimalFormat("#,###");
            ventas.add(new Venta(idVenta, fecha, detalle.toString(), df.format(total) + " COP"));
            guardar();
        }

        public List<Venta> obtenerVentas() {
            return ventas;
        }
    }

    // ================= UTILIDADES VISUALES =================
    private Image cargarImagen(String rutaClasspath) {
        try (InputStream is = getClass().getResourceAsStream(rutaClasspath)) {
            if (is == null)
                return null;
            return new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    private void configurarIcono(Stage stage) {
        Image icono = cargarImagen(LOGO);
        if (icono != null)
            stage.getIcons().add(icono);
    }

    private void aplicarFondo(Region root, String rutaClasspath) {
        Image img = cargarImagen(rutaClasspath);
        if (img == null) {
            root.setStyle("-fx-background-color: #f0f0f0;");
            return;
        }
        BackgroundSize size = new BackgroundSize(100, 100, true, true, false, true);
        root.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size)));
    }

    private StackPane crearFondo(Node contenido, String rutaClasspath) {
        Image img = cargarImagen(rutaClasspath);
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
        configurarIcono((Stage) a.getDialogPane().getScene().getWindow());
        a.show();
        new Thread(() -> {
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
            }
            Platform.runLater(() -> {
                if (a.isShowing())
                    a.close();
            });
        }).start();
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
        VBox root = new VBox(10);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        aplicarFondo(root, FONDO_LOGIN);
        Label titulo = new Label("Stock-Sync");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titulo.setTextFill(Color.web("#0F2C54"));
        TextField txtUser = new TextField();
        txtUser.setPromptText("Usuario");
        txtUser.setMaxWidth(280);
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Contraseña");
        txtPass.setMaxWidth(280);
        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setDefaultButton(true);
        btnLogin.setStyle("-fx-background-color:#0F2C54; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 20;");
        Button btnReg = new Button("Registrarse");
        btnReg.setStyle("-fx-background-color:#0F2C54; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 20;");
        Button btnCat = new Button("Ver Catálogo");
        btnCat.setStyle("-fx-background-color:#6c7172; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 20;");
        HBox botones = new HBox(10, btnLogin, btnReg);
        botones.setAlignment(Pos.CENTER);
        Label olvide = new Label("¿Olvidaste tu contraseña?");
        olvide.setTextFill(Color.web("#17a2b8"));
        olvide.setUnderline(true);
        olvide.setStyle("-fx-cursor:hand;");

        btnLogin.setOnAction(e -> {
            Usuario u = usuarioService.validar(txtUser.getText(), txtPass.getText());
            if (u == null)
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario/clave inválidos");
            else if ("admin".equals(u.getRol())) {
                s.hide();
                mostrarAdmin();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Bienvenido Administrador");
            } else {
                s.hide();
                mostrarCliente();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Bienvenido Cliente");
            }
        });
        btnReg.setOnAction(e -> {
            s.hide();
            mostrarRegistro();
        });
        btnCat.setOnAction(e -> {
            s.hide();
            mostrarCatalogoPublico(s);
        });
        olvide.setOnMouseClicked(e -> mostrarRecuperar(s));
        root.getChildren().addAll(titulo, txtUser, txtPass, botones, btnCat, olvide);
        s.setScene(new Scene(crearFondo(root, FONDO_LOGIN), 380, 380));
        s.show();
    }

    private void mostrarRegistro() {
        Stage s = new Stage();
        s.setTitle("Registro de Cliente");
        configurarIcono(s);
        VBox root = new VBox(8);
        root.setPadding(new Insets(20));
        aplicarFondo(root, FONDO_LOGIN);
        Label titulo = new Label("Registro de Nuevo Cliente");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        TextField txtN = new TextField();
        txtN.setPromptText("Nombres");
        TextField txtA = new TextField();
        txtA.setPromptText("Apellidos");
        TextField txtC = new TextField();
        txtC.setPromptText("Cédula");
        TextField txtE = new TextField();
        txtE.setPromptText("Correo Electrónico");
        TextField txtU = new TextField();
        txtU.setPromptText("Nombre de Usuario");
        PasswordField txtP = new PasswordField();
        txtP.setPromptText("Contraseña");
        Button btnReg = new Button("Registrarse como Cliente");
        btnReg.setDefaultButton(true);
        btnReg.setStyle("-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold;");
        Button btnCan = new Button("Cancelar");
        btnReg.setOnAction(e -> {
            String n = txtN.getText().trim(), a = txtA.getText().trim(), c = txtC.getText().trim(),
                    em = txtE.getText().trim(), u = txtU.getText().trim(), p = txtP.getText().trim();
            if (n.isEmpty() || a.isEmpty() || c.isEmpty() || em.isEmpty() || u.isEmpty() || p.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Completa todos los campos");
                return;
            }
            if (usuarioService.existeUsuario(u)) {
                mostrarAlerta(Alert.AlertType.ERROR, "El nombre de usuario ya existe");
                return;
            }
            usuarioService.registrar(new Usuario(u, p, "cliente", n, a, c, em));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente registrado correctamente");
            s.close();
            loginStage.show();
        });
        btnCan.setOnAction(e -> {
            s.close();
            loginStage.show();
        });
        root.getChildren().addAll(titulo, txtN, txtA, txtC, txtE, txtU, txtP, btnReg, btnCan);
        s.setScene(new Scene(crearFondo(root, FONDO_GENERAL), 400, 450));
        s.show();
    }

    private void mostrarRecuperar(Stage parent) {
        Stage s = new Stage();
        s.setTitle("Recuperar Contraseña");
        configurarIcono(s);
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        Label t = new Label("Recuperar Contraseña");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        TextField txt = new TextField();
        txt.setPromptText("Usuario o email");
        Button btn = new Button("Enviar");
        btn.setStyle("-fx-background-color:#0F2C54; -fx-text-fill:white;");
        Button can = new Button("Cerrar");
        btn.setOnAction(e -> {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Instrucciones enviadas a su correo");
            s.close();
        });
        can.setOnAction(e -> s.close());
        root.getChildren().addAll(t, txt, btn, can);
        s.setScene(new Scene(root, 350, 200));
        s.show();
    }

    private void mostrarCatalogoPublico(Stage parent) {
        Stage s = new Stage();
        s.setTitle("Catálogo Público");
        configurarIcono(s);
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        aplicarFondo(root, FONDO_CLIENTE);
        Label t = new Label("Inventario Disponible");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        t.setTextFill(Color.web("#FF4500"));
        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll(inventarioService.obtenerCategorias());
        cbCat.setValue("Todas");
        TextField txt = new TextField();
        txt.setPromptText("Buscar...");
        HBox filtros = new HBox(10, cbCat, txt);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(6);
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
        grid.add(new Label("Precio Unidad") {
            {
                setStyle(bold);
            }
        }, 4, 0);
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        Button btnBack = new Button("Regresar");
        btnBack.setStyle("-fx-background-color:#6c757d; -fx-text-fill:white;");
        btnBack.setOnAction(e -> {
            s.close();
            parent.show();
        });
        Runnable actualizar = () -> {
            grid.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
            List<Producto> lista = inventarioService.filtrar(txt.getText(), cbCat.getValue());
            int r = 1;
            for (Producto p : lista) {
                grid.add(new Label(p.getId()), 0, r);
                grid.add(new Label(p.getNombre()), 1, r);
                grid.add(new Label(p.getCategoria()), 2, r);
                Label stk = new Label(String.valueOf(p.getCantidad()));
                GridPane.setHalignment(stk, HPos.CENTER);
                grid.add(stk, 3, r);
                grid.add(new Label(df().format(p.getPrecio()) + " COP"), 4, r);
                r++;
            }
        };
        cbCat.setOnAction(e -> actualizar.run());
        txt.textProperty().addListener((o, ov, nv) -> actualizar.run());
        actualizar.run();
        root.getChildren().addAll(t, filtros, scroll, btnBack);
        s.setScene(new Scene(crearFondo(root, FONDO_CLIENTE), 700, 500));
        s.show();
    }

    private void mostrarCliente() {
        Stage s = new Stage();
        s.setTitle("Stock-Sync - Catálogo de Productos");
        configurarIcono(s);
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        aplicarFondo(root, FONDO_CLIENTE);
        Label t = new Label("Catálogo de Productos");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll(inventarioService.obtenerCategorias());
        cbCat.setValue("Todas");
        TextField txt = new TextField();
        txt.setPromptText("Buscar...");
        HBox filtros = new HBox(10, cbCat, txt);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);
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
        grid.add(new Label("Cantidad") {
            {
                setStyle(bold);
            }
        }, 4, 0);
        grid.add(new Label("Precio Unidad") {
            {
                setStyle(bold);
            }
        }, 5, 0);
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        totalPrefix = new Text("Total: $");
        totalPrefix.setFill(Color.web(TOTAL_COLOR));
        totalPrefix.setFont(Font.font("Arial", FontWeight.BOLD, TOTAL_FONT_SIZE));
        totalValue = new Text("0 COP");
        totalValue.setFill(Color.BLACK);
        totalValue.setFont(Font.font("Arial", FontWeight.BOLD, TOTAL_FONT_SIZE));
        TextFlow totalFlow = new TextFlow(totalPrefix, totalValue);
        Button btnPay = new Button("Pagar");
        btnPay.setStyle("-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 20;");
        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.setStyle("-fx-background-color:#dc3545; -fx-text-fill:white; -fx-font-weight:bold;");
        HBox bottom = new HBox(20, totalFlow, btnPay, btnLogout);
        bottom.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(totalFlow, javafx.scene.layout.Priority.ALWAYS);
        HBox.setMargin(btnLogout, new Insets(0, 0, 0, 10));

        Runnable render = () -> {
            grid.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
            List<Producto> lista = inventarioService.filtrar(txt.getText(), cbCat.getValue());
            int r = 1;
            for (Producto p : lista) {
                grid.add(new Label(p.getId()), 0, r);
                grid.add(new Label(p.getNombre()), 1, r);
                grid.add(new Label(p.getCategoria()), 2, r);
                Label stk = new Label(String.valueOf(p.getCantidad()));
                GridPane.setHalignment(stk, HPos.CENTER);
                grid.add(stk, 3, r);
                TextField tf = new TextField();
                tf.setPromptText("0");
                tf.setPrefWidth(70);
                tf.setPrefHeight(25);
                tf.focusedProperty().addListener((obs, ov, nv) -> {
                    if (nv && "0".equals(tf.getText()))
                        tf.clear();
                });
                tf.textProperty().addListener((obs, ov, nv) -> {
                    if ("0".equals(nv))
                        tf.clear();
                    carrito.removeIf(x -> x.getId().equals(p.getId()));
                    try {
                        int c = nv.isEmpty() ? 0 : Integer.parseInt(nv);
                        if (c > 0)
                            carrito.add(new Producto(p.getId(), p.getNombre(), p.getCategoria(), c, c * p.getPrecio()));
                    } catch (NumberFormatException ex) {
                    }
                    actualizarTotal();
                });
                grid.add(tf, 4, r);
                grid.add(new Label(df().format(p.getPrecio()) + " COP"), 5, r);
                r++;
            }
        };
        cbCat.setOnAction(e -> render.run());
        txt.textProperty().addListener((o, ov, nv) -> render.run());
        render.run();

        btnPay.setOnAction(e -> mostrarPago(s));
        btnLogout.setOnAction(e -> {
            carrito.clear();
            actualizarTotal();
            s.close();
            loginStage.show();
        });
        root.getChildren().addAll(t, filtros, scroll, bottom);
        s.setScene(new Scene(crearFondo(root, FONDO_CLIENTE), 800, 600));
        s.show();
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = carrito.stream().mapToDouble(Producto::getPrecio).sum();
        totalValue.setText(df().format(total) + " COP");
    }

    private void mostrarPago(Stage clientStage) {
        Stage s = new Stage();
        s.setTitle("Método de Pago");
        configurarIcono(s);
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        Label t = new Label("Método de Pago");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        t.setTextFill(Color.web("#0F2C54"));
        Button btnCard = new Button("💳 Tarjeta de Crédito/Débito");
        btnCard.setStyle("-fx-background-color:#343a40; -fx-text-fill:white; -fx-font-size:14px; -fx-padding:12 20;");
        Button btnEfecty = new Button("🏦 Consignación en Efecty");
        btnEfecty.setStyle("-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-size:14px; -fx-padding:12 20;");
        btnCard.setMaxWidth(Double.MAX_VALUE);
        btnEfecty.setMaxWidth(Double.MAX_VALUE);
        btnCard.setOnAction(e -> {
            s.close();
            mostrarPagoTarjeta(clientStage);
        });
        btnEfecty.setOnAction(e -> {
            if (carrito.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "El carrito está vacío");
                return;
            }
            if (!procesarVenta()) {
                mostrarAlerta(Alert.AlertType.ERROR, "No hay suficiente stock disponible");
                return;
            }
            String codigo = String.valueOf(new Random().nextInt(900000) + 100000);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Consignación en Efecty");
            a.setHeaderText("Guarde este código de consignación:");
            Label lbl = new Label(codigo);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 32));
            lbl.setTextFill(Color.web("#FF4500"));
            a.getDialogPane().setContent(lbl);
            configurarIcono((Stage) a.getDialogPane().getScene().getWindow());
            a.showAndWait();
            carrito.clear();
            actualizarTotal();
            s.close();
            clientStage.close();
            mostrarCliente();
        });
        root.getChildren().addAll(t, btnCard, btnEfecty);
        s.setScene(new Scene(root, 400, 250));
        s.show();
    }

    private void mostrarPagoTarjeta(Stage clientStage) {
        Stage s = new Stage();
        s.setTitle("Pago con Tarjeta");
        configurarIcono(s);
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        Label t = new Label("Pago con Tarjeta");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Visa", "Mastercard", "American Express", "Discover", "Diners Club");
        cbTipo.setPromptText("Tipo de Tarjeta");
        TextField txtNum = new TextField();
        txtNum.setPromptText("Número de tarjeta");
        txtNum.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() <= 19 ? change : null));
        TextField txtTit = new TextField();
        txtTit.setPromptText("Nombre del titular");
        TextField txtExp = new TextField();
        txtExp.setPromptText("MM/AA");
        txtExp.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() <= 5 ? change : null));
        TextField txtCvv = new TextField();
        txtCvv.setPromptText("CVV");
        txtCvv.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().length() <= 4 ? change : null));
        Button btn = new Button("Confirmar Pago");
        btn.setStyle("-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            if (cbTipo.getValue() == null || txtNum.getText().trim().isEmpty() || txtTit.getText().trim().isEmpty()
                    || txtExp.getText().trim().isEmpty() || txtCvv.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Complete todos los campos");
                return;
            }
            if (carrito.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "El carrito está vacío");
                return;
            }
            if (!procesarVenta()) {
                mostrarAlerta(Alert.AlertType.ERROR, "No hay suficiente stock disponible");
                return;
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("¡Pago Exitoso!");
            a.setHeaderText("Gracias por su compra");
            a.setContentText("Su pago con " + cbTipo.getValue() + " ha sido procesado correctamente.");
            configurarIcono((Stage) a.getDialogPane().getScene().getWindow());
            a.showAndWait();
            carrito.clear();
            actualizarTotal();
            s.close();
            clientStage.close();
            mostrarCliente();
        });
        root.getChildren().addAll(t, cbTipo, txtNum, txtTit, txtExp, txtCvv, btn);
        s.setScene(new Scene(root, 380, 380));
        s.show();
    }

    private boolean procesarVenta() {
        for (Producto c : carrito) {
            for (Producto p : inventarioService.obtenerInventario()) {
                if (p.getId().equals(c.getId()) && p.getCantidad() < c.getCantidad())
                    return false;
            }
        }
        double totalVenta = 0;
        for (Producto c : carrito) {
            for (Producto p : inventarioService.obtenerInventario()) {
                if (p.getId().equals(c.getId())) {
                    int nueva = p.getCantidad() - c.getCantidad();
                    inventarioService.actualizarProducto(
                            new Producto(p.getId(), p.getNombre(), p.getCategoria(), nueva, p.getPrecio()));
                    totalVenta += c.getPrecio();
                    break;
                }
            }
        }
        ventasService.registrarVenta(carrito, totalVenta);
        return true;
    }

    // ================= ADMIN CON REPORTES CSV Y CARGA MASIVA =================
    private void mostrarAdmin() {
        Stage s = new Stage();
        s.setTitle("Stock-Sync - Panel Administrador");
        configurarIcono(s);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        aplicarFondo(root, FONDO_ADMIN);
        Label t = new Label("Panel Administrador");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        t.setTextFill(Color.web("#0F2C54"));
        root.setTop(t);
        TabPane tabs = new TabPane();
        String bold = "-fx-font-weight:bold;";

        // TAB 1: INVENTARIO
        Tab tabInv = new Tab("Inventario");
        tabInv.setClosable(false);
        VBox invBox = new VBox(10);
        invBox.setPadding(new Insets(10));
        ComboBox<String> cbCatA = new ComboBox<>();
        cbCatA.getItems().addAll(inventarioService.obtenerCategorias());
        cbCatA.setValue("Todas");
        TextField txtA = new TextField();
        txtA.setPromptText("Buscar...");
        HBox filtA = new HBox(10, cbCatA, txtA);
        GridPane gridA = new GridPane();
        gridA.setHgap(10);
        gridA.setVgap(6);
        gridA.add(new Label("ID") {
            {
                setStyle(bold);
            }
        }, 0, 0);
        gridA.add(new Label("Nombre") {
            {
                setStyle(bold);
            }
        }, 1, 0);
        gridA.add(new Label("Categoría") {
            {
                setStyle(bold);
            }
        }, 2, 0);
        gridA.add(new Label("Stock") {
            {
                setStyle(bold);
            }
        }, 3, 0);
        gridA.add(new Label("Precio") {
            {
                setStyle(bold);
            }
        }, 4, 0);
        ScrollPane scrollA = new ScrollPane(gridA);
        scrollA.setFitToWidth(true);
        scrollA.setPrefHeight(300);
        Runnable updA = () -> {
            gridA.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
            List<Producto> lista = inventarioService.filtrar(txtA.getText(), cbCatA.getValue());
            int r = 1;
            for (Producto p : lista) {
                gridA.add(new Label(p.getId()), 0, r);
                gridA.add(new Label(p.getNombre()), 1, r);
                gridA.add(new Label(p.getCategoria()), 2, r);
                Label stk = new Label(String.valueOf(p.getCantidad()));
                GridPane.setHalignment(stk, HPos.CENTER);
                gridA.add(stk, 3, r);
                gridA.add(new Label(df().format(p.getPrecio()) + " COP"), 4, r);
                r++;
            }
        };
        cbCatA.setOnAction(e -> updA.run());
        txtA.textProperty().addListener((o, ov, nv) -> updA.run());
        updA.run();

        Button btnImportInv = new Button("📂 Cargar Inventario CSV");
        btnImportInv.setStyle("-fx-background-color:#17a2b8; -fx-text-fill:white; -fx-font-weight:bold;");
        btnImportInv.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Cargar Inventario");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv", "*.txt"));
            File f = fc.showOpenDialog(s);
            if (f != null) {
                int c = inventarioService.importarInventarioCSV(f);
                updA.run();
                cbCatA.getItems().clear();
                cbCatA.getItems().addAll(inventarioService.obtenerCategorias());
                cbCatA.setValue("Todas");
                mostrarAlerta(Alert.AlertType.INFORMATION, c + " productos cargados exitosamente.");
            }
        });
        Button btnExportInv = new Button("💾 Exportar Inventario CSV");
        btnExportInv.setStyle("-fx-background-color:#6c757d; -fx-text-fill:white; -fx-font-weight:bold;");
        btnExportInv.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar Inventario");
            fc.setInitialFileName("inventario_backup.csv");
            File f = fc.showSaveDialog(s);
            if (f != null) {
                inventarioService.exportarInventarioCSV(f);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Inventario exportado exitosamente.");
            }
        });
        HBox csvBoxInv = new HBox(10, btnImportInv, btnExportInv);
        invBox.getChildren().addAll(new Label("Inventario"), filtA, scrollA, csvBoxInv);
        tabInv.setContent(invBox);

        // TAB 2: AGREGAR
        Tab tabAdd = new Tab("Agregar");
        tabAdd.setClosable(false);
        VBox addBox = new VBox(10);
        addBox.setPadding(new Insets(10));
        TextField addId = new TextField();
        addId.setPromptText("ID");
        TextField addNom = new TextField();
        addNom.setPromptText("Nombre");
        ComboBox<String> addCat = new ComboBox<>();
        addCat.setPromptText("Categoría");
        addCat.setEditable(true);
        addCat.getItems().addAll(inventarioService.obtenerCategorias().stream().filter(c -> !"Todas".equals(c))
                .collect(Collectors.toList()));
        TextField addQty = new TextField();
        addQty.setPromptText("Cantidad");
        TextField addPr = new TextField();
        addPr.setPromptText("Precio");
        Button btnAdd = new Button("Agregar");
        btnAdd.setStyle("-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold;");
        btnAdd.setOnAction(e -> {
            try {
                String id = addId.getText().trim(), nom = addNom.getText().trim(), cat = addCat.getValue();
                int qty = Integer.parseInt(addQty.getText().trim());
                double pr = Double.parseDouble(addPr.getText().trim());
                if (id.isEmpty() || nom.isEmpty() || cat == null || cat.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Complete todos los campos");
                    return;
                }
                if (inventarioService.obtenerInventario().stream().anyMatch(p -> p.getId().equals(id))) {
                    mostrarAlerta(Alert.AlertType.ERROR, "El ID ya existe");
                    return;
                }
                inventarioService.agregarProducto(new Producto(id, nom, cat, qty, pr));
                mostrarAlerta(Alert.AlertType.INFORMATION, "Producto agregado y guardado");
                addId.clear();
                addNom.clear();
                addCat.setValue(null);
                addQty.clear();
                addPr.clear();
                updA.run();
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Cantidad y precio deben ser números");
            }
        });
        addBox.getChildren().addAll(new Label("Agregar Producto"), addId, addNom, addCat, addQty, addPr, btnAdd);
        tabAdd.setContent(addBox);

        // TAB 3: ACTUALIZAR
        Tab tabUpd = new Tab("Actualizar");
        tabUpd.setClosable(false);
        VBox updBox = new VBox(10);
        updBox.setPadding(new Insets(10));
        TextField updId = new TextField();
        updId.setPromptText("ID del Producto a Actualizar (Obligatorio)");
        TextField updNom = new TextField();
        updNom.setPromptText("Nuevo Nombre (Dejar en blanco para no cambiar)");
        ComboBox<String> updCat = new ComboBox<>();
        updCat.setPromptText("Nueva Categoría");
        updCat.setEditable(true);
        updCat.getItems().addAll(inventarioService.obtenerCategorias().stream().filter(c -> !"Todas".equals(c))
                .collect(Collectors.toList()));
        TextField updQty = new TextField();
        updQty.setPromptText("Nueva Cantidad");
        TextField updPr = new TextField();
        updPr.setPromptText("Nuevo Precio");
        Button btnUpd = new Button("Actualizar Producto");
        btnUpd.setStyle("-fx-background-color:#007bff; -fx-text-fill:white; -fx-font-weight:bold;");
        btnUpd.setOnAction(e -> {
            String id = updId.getText().trim();
            if (id.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Debe ingresar el ID del producto que desea actualizar.");
                return;
            }
            Producto existente = inventarioService.obtenerInventario().stream().filter(p -> p.getId().equals(id))
                    .findFirst().orElse(null);
            if (existente == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "No se encontró ningún producto con el ID: " + id);
                return;
            }
            try {
                String nomFinal = updNom.getText().trim().isEmpty() ? existente.getNombre() : updNom.getText().trim();
                String catFinal = existente.getCategoria();
                if (updCat.getValue() != null && !updCat.getValue().trim().isEmpty())
                    catFinal = updCat.getValue().trim();
                int qtyFinal = existente.getCantidad();
                if (!updQty.getText().trim().isEmpty())
                    qtyFinal = Integer.parseInt(updQty.getText().trim());
                double prFinal = existente.getPrecio();
                if (!updPr.getText().trim().isEmpty())
                    prFinal = Double.parseDouble(updPr.getText().trim());
                inventarioService.actualizarProducto(new Producto(id, nomFinal, catFinal, qtyFinal, prFinal));
                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Producto actualizado y guardado en inventario.txt correctamente.");
                updId.clear();
                updNom.clear();
                updCat.setValue(null);
                updQty.clear();
                updPr.clear();
                updA.run();
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "La cantidad y el precio deben ser números válidos.");
            }
        });
        updBox.getChildren().addAll(new Label("Actualizar Producto"), updId, updNom, updCat, updQty, updPr, btnUpd);
        tabUpd.setContent(updBox);

        // TAB 4: ELIMINAR
        Tab tabDel = new Tab("Eliminar");
        tabDel.setClosable(false);
        VBox delBox = new VBox(10);
        delBox.setPadding(new Insets(10));
        TextField delId = new TextField();
        delId.setPromptText("ID a eliminar");
        Button btnDel = new Button("Eliminar");
        btnDel.setStyle("-fx-background-color:#dc3545; -fx-text-fill:white; -fx-font-weight:bold;");
        btnDel.setOnAction(e -> {
            String id = delId.getText().trim();
            if (id.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Ingrese un ID");
                return;
            }
            inventarioService.eliminarProducto(id);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Producto eliminado y guardado");
            delId.clear();
            updA.run();
        });
        delBox.getChildren().addAll(new Label("Eliminar Producto"), delId, btnDel);
        tabDel.setContent(delBox);

        // ================= TAB 5: REPORTE DE EXISTENCIAS (CSV) =================
        Tab tabRepExist = new Tab("📊 Reporte de Existencias");
        tabRepExist.setClosable(false);
        VBox boxRepExist = new VBox(10);
        boxRepExist.setPadding(new Insets(15));
        TextArea txtRepExist = new TextArea();
        txtRepExist.setEditable(false);
        txtRepExist.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 13px;");

        Button btnGenExist = new Button("Generar Reporte");
        btnGenExist.setStyle("-fx-background-color:#17a2b8; -fx-text-fill:white; -fx-font-weight:bold;");
        btnGenExist.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("ID,NOMBRE,CATEGORIA,CANTIDAD,PRECIO,VALOR_TOTAL\n");
            double valorTotalInventario = 0;
            for (Producto p : inventarioService.obtenerInventario()) {
                double valorProducto = p.getCantidad() * p.getPrecio();
                sb.append(String.format("%s,%s,%s,%d,%.0f,%.0f\n",
                        p.getId(), p.getNombre(), p.getCategoria(), p.getCantidad(), p.getPrecio(), valorProducto));
                valorTotalInventario += valorProducto;
            }
            sb.append(String.format("\nTOTAL GENERAL,,,,%d productos,%.0f COP",
                    inventarioService.obtenerInventario().size(), valorTotalInventario));
            txtRepExist.setText(sb.toString());
        });

        Button btnExpExist = new Button("💾 Exportar a CSV");
        btnExpExist.setStyle("-fx-background-color:#6c757d; -fx-text-fill:white; -fx-font-weight:bold;");
        btnExpExist.setOnAction(e -> {
            if (txtRepExist.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Primero genera el reporte");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Existencias");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
            fileChooser.setInitialFileName("reporte_existencias_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".csv");
            File archivo = fileChooser.showSaveDialog(s);
            if (archivo != null) {
                try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
                    writer.print(txtRepExist.getText());
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Reporte exportado exitosamente a:\n" + archivo.getAbsolutePath());
                } catch (Exception ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar: " + ex.getMessage());
                }
            }
        });
        boxRepExist.getChildren().addAll(btnGenExist, txtRepExist, btnExpExist);
        tabRepExist.setContent(boxRepExist);

        // ================= TAB 6: REPORTE DE VENTAS (CSV) =================
        Tab tabRepVentas = new Tab("💰 Reporte de Ventas");
        tabRepVentas.setClosable(false);
        VBox boxRepVentas = new VBox(10);
        boxRepVentas.setPadding(new Insets(15));
        TextArea txtRepVentas = new TextArea();
        txtRepVentas.setEditable(false);
        txtRepVentas.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 13px;");

        Button btnGenVentas = new Button("Generar Reporte");
        btnGenVentas.setStyle("-fx-background-color:#17a2b8; -fx-text-fill:white; -fx-font-weight:bold;");
        btnGenVentas.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("ID_VENTA,FECHA,PRODUCTOS,TOTAL\n");
            double totalGeneralVentas = 0;
            for (Venta v : ventasService.obtenerVentas()) {
                sb.append(String.format("%s,%s,\"%s\",%s\n", v.getIdVenta(), v.getFecha(), v.getDetalleProductos(),
                        v.getTotal().replace(" COP", "").replace(",", "")));
                String totalLimpio = v.getTotal().replace(" COP", "").replace(",", "");
                totalGeneralVentas += Double.parseDouble(totalLimpio);
            }
            sb.append(String.format("\nTOTAL GENERAL,,,,%.0f COP", totalGeneralVentas));
            txtRepVentas.setText(sb.toString());
        });

        Button btnExpVentas = new Button("💾 Exportar a CSV");
        btnExpVentas.setStyle("-fx-background-color:#6c757d; -fx-text-fill:white; -fx-font-weight:bold;");
        btnExpVentas.setOnAction(e -> {
            if (txtRepVentas.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Primero genera el reporte");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Ventas");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
            fileChooser.setInitialFileName("reporte_ventas_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".csv");
            File archivo = fileChooser.showSaveDialog(s);
            if (archivo != null) {
                try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
                    writer.print(txtRepVentas.getText());
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Reporte exportado exitosamente a:\n" + archivo.getAbsolutePath());
                } catch (Exception ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar: " + ex.getMessage());
                }
            }
        });
        boxRepVentas.getChildren().addAll(btnGenVentas, txtRepVentas, btnExpVentas);
        tabRepVentas.setContent(boxRepVentas);

        // TAB 7: USUARIOS
        Tab tabUsers = new Tab("Usuarios");
        tabUsers.setClosable(false);
        VBox usersBox = new VBox(15);
        usersBox.setPadding(new Insets(15));
        Label addTitle = new Label("Agregar Nuevo Usuario");
        addTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        addTitle.setTextFill(Color.web("#0F2C54"));
        GridPane addGrid = new GridPane();
        addGrid.setHgap(10);
        addGrid.setVgap(8);
        TextField addNombres = new TextField();
        addNombres.setPromptText("Nombres");
        TextField addApellidos = new TextField();
        addApellidos.setPromptText("Apellidos");
        TextField addCedula = new TextField();
        addCedula.setPromptText("Cédula");
        TextField addEmail = new TextField();
        addEmail.setPromptText("Correo Electrónico");
        TextField addUser = new TextField();
        addUser.setPromptText("Nombre de Usuario");
        PasswordField addPass = new PasswordField();
        addPass.setPromptText("Contraseña");
        ComboBox<String> addRol = new ComboBox<>();
        addRol.getItems().addAll("cliente", "admin");
        addRol.setValue("cliente");
        addGrid.add(new Label("Nombres:"), 0, 0);
        addGrid.add(addNombres, 1, 0);
        addGrid.add(new Label("Apellidos:"), 0, 1);
        addGrid.add(addApellidos, 1, 1);
        addGrid.add(new Label("Cédula:"), 0, 2);
        addGrid.add(addCedula, 1, 2);
        addGrid.add(new Label("Email:"), 0, 3);
        addGrid.add(addEmail, 1, 3);
        addGrid.add(new Label("Usuario:"), 0, 4);
        addGrid.add(addUser, 1, 4);
        addGrid.add(new Label("Contraseña:"), 0, 5);
        addGrid.add(addPass, 1, 5);
        addGrid.add(new Label("Rol:"), 0, 6);
        addGrid.add(addRol, 1, 6);
        Label listTitle = new Label("Usuarios Registrados");
        listTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        listTitle.setTextFill(Color.web("#0F2C54"));
        GridPane gridUsers = new GridPane();
        gridUsers.setHgap(10);
        gridUsers.setVgap(6);
        gridUsers.add(new Label("Usuario") {
            {
                setStyle(bold);
            }
        }, 0, 0);
        gridUsers.add(new Label("Nombres") {
            {
                setStyle(bold);
            }
        }, 1, 0);
        gridUsers.add(new Label("Apellidos") {
            {
                setStyle(bold);
            }
        }, 2, 0);
        gridUsers.add(new Label("Cédula") {
            {
                setStyle(bold);
            }
        }, 3, 0);
        gridUsers.add(new Label("Email") {
            {
                setStyle(bold);
            }
        }, 4, 0);
        gridUsers.add(new Label("Rol") {
            {
                setStyle(bold);
            }
        }, 5, 0);
        ScrollPane scrollUsers = new ScrollPane(gridUsers);
        scrollUsers.setFitToWidth(true);
        scrollUsers.setPrefHeight(250);
        Runnable updUsers = () -> {
            gridUsers.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
            List<Usuario> lista = usuarioService.obtenerUsuarios();
            int r = 1;
            for (Usuario u : lista) {
                gridUsers.add(new Label(u.getUsername()), 0, r);
                gridUsers.add(new Label(u.getNombres() != null ? u.getNombres() : "-"), 1, r);
                gridUsers.add(new Label(u.getApellidos() != null ? u.getApellidos() : "-"), 2, r);
                gridUsers.add(new Label(u.getCedula() != null ? u.getCedula() : "-"), 3, r);
                gridUsers.add(new Label(u.getEmail() != null ? u.getEmail() : "-"), 4, r);
                Label lblRol = new Label(u.getRol().toUpperCase());
                lblRol.setStyle("admin".equals(u.getRol()) ? "-fx-text-fill:#dc3545; -fx-font-weight:bold;"
                        : "-fx-text-fill:#28a745; -fx-font-weight:bold;");
                GridPane.setHalignment(lblRol, HPos.CENTER);
                gridUsers.add(lblRol, 5, r);
                r++;
            }
        };
        updUsers.run();
        Button btnAddUser = new Button("Registrar Usuario");
        btnAddUser
                .setStyle("-fx-background-color:#007bff; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 15;");
        btnAddUser.setOnAction(e -> {
            String n = addNombres.getText().trim(), a = addApellidos.getText().trim(), c = addCedula.getText().trim(),
                    em = addEmail.getText().trim(), u = addUser.getText().trim(), p = addPass.getText().trim(),
                    rol = addRol.getValue();
            if (n.isEmpty() || a.isEmpty() || c.isEmpty() || em.isEmpty() || u.isEmpty() || p.isEmpty()
                    || rol == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Complete todos los campos");
                return;
            }
            if ("admin".equals(rol) && !em.toLowerCase().endsWith("@stocksync.com")) {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Los administradores deben registrarse con un correo @stocksync.com");
                return;
            }
            if (usuarioService.existeUsuario(u)) {
                mostrarAlerta(Alert.AlertType.ERROR, "El nombre de usuario ya existe");
                return;
            }
            usuarioService.registrar(new Usuario(u, p, rol, n, a, c, em));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario registrado correctamente");
            addNombres.clear();
            addApellidos.clear();
            addCedula.clear();
            addEmail.clear();
            addUser.clear();
            addPass.clear();
            addRol.setValue("cliente");
            updUsers.run();
        });

        Button btnImportUser = new Button("📂 Cargar Usuarios CSV");
        btnImportUser.setStyle("-fx-background-color:#17a2b8; -fx-text-fill:white; -fx-font-weight:bold;");
        btnImportUser.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Cargar Usuarios");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv", "*.txt"));
            File f = fc.showOpenDialog(s);
            if (f != null) {
                int c = usuarioService.importarCSV(f);
                updUsers.run();
                mostrarAlerta(Alert.AlertType.INFORMATION, c + " usuarios cargados exitosamente.");
            }
        });
        Button btnExportUser = new Button("💾 Exportar Usuarios CSV");
        btnExportUser.setStyle("-fx-background-color:#6c757d; -fx-text-fill:white; -fx-font-weight:bold;");
        btnExportUser.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar Usuarios");
            fc.setInitialFileName("usuarios_backup.csv");
            File f = fc.showSaveDialog(s);
            if (f != null) {
                usuarioService.exportarCSV(f);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuarios exportados exitosamente.");
            }
        });
        HBox csvBoxUser = new HBox(10, btnImportUser, btnExportUser);
        usersBox.getChildren().addAll(addTitle, addGrid, btnAddUser, csvBoxUser, listTitle, scrollUsers);
        tabUsers.setContent(usersBox);

        // TAB 8: PROVEEDORES
        Tab tabProv = new Tab("Proveedores");
        tabProv.setClosable(false);
        VBox provBox = new VBox(10);
        provBox.setPadding(new Insets(10));
        TextField provN = new TextField();
        provN.setPromptText("Nombre Proveedor");
        TextField provE = new TextField();
        provE.setPromptText("Email Proveedor");
        Button btnProv = new Button("Agregar");
        btnProv.setStyle("-fx-background-color:#28a745; -fx-text-fill:white; -fx-font-weight:bold;");
        ListView<String> lstProv = new ListView<>();
        lstProv.setPrefHeight(200);
        Runnable updProv = () -> {
            lstProv.getItems().clear();
            for (Proveedor p : inventarioService.obtenerProveedores())
                lstProv.getItems().add(p.toString());
        };
        btnProv.setOnAction(e -> {
            String n = provN.getText().trim(), em = provE.getText().trim();
            if (n.isEmpty() || em.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Complete proveedor");
                return;
            }
            inventarioService.agregarProveedor(new Proveedor(n, em));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Proveedor agregado y guardado");
            provN.clear();
            provE.clear();
            updProv.run();
        });
        updProv.run();

        Button btnImportProv = new Button("📂 Cargar Proveedores CSV");
        btnImportProv.setStyle("-fx-background-color:#17a2b8; -fx-text-fill:white; -fx-font-weight:bold;");
        btnImportProv.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Cargar Proveedores");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv", "*.txt"));
            File f = fc.showOpenDialog(s);
            if (f != null) {
                int c = inventarioService.importarProveedoresCSV(f);
                updProv.run();
                mostrarAlerta(Alert.AlertType.INFORMATION, c + " proveedores cargados exitosamente.");
            }
        });
        Button btnExportProv = new Button("💾 Exportar Proveedores CSV");
        btnExportProv.setStyle("-fx-background-color:#6c757d; -fx-text-fill:white; -fx-font-weight:bold;");
        btnExportProv.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar Proveedores");
            fc.setInitialFileName("proveedores_backup.csv");
            File f = fc.showSaveDialog(s);
            if (f != null) {
                inventarioService.exportarProveedoresCSV(f);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Proveedores exportados exitosamente.");
            }
        });
        HBox csvBoxProv = new HBox(10, btnImportProv, btnExportProv);
        provBox.getChildren().addAll(new Label("Proveedores"), provN, provE, btnProv, lstProv, csvBoxProv);
        tabProv.setContent(provBox);

        tabs.getTabs().addAll(tabInv, tabAdd, tabUpd, tabDel, tabRepExist, tabRepVentas, tabUsers, tabProv);
        root.setCenter(tabs);

        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.setStyle("-fx-background-color:#dc3545; -fx-text-fill:white; -fx-font-weight:bold;");
        btnLogout.setOnAction(e -> {
            s.close();
            loginStage.show();
        });
        HBox bottom = new HBox(btnLogout);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(5, 0, 0, 0));
        root.setBottom(bottom);
        s.setScene(new Scene(crearFondo(root, FONDO_ADMIN), 900, 700));
        s.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}