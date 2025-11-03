package proyecto;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import proyecto.dao.ProductoDAO;
import proyecto.excepciones.DatosInvalidosException;
import proyecto.modelos.*;
import proyecto.dao.PedidoDao;
import proyecto.dao.UsuarioDao;

public class proyectointeger {
    
    private static final Administrador admin = new Administrador(1234, null, "Pepe", "Peréz", "pepe_perez@hotmail.com", 0);
    private static final TiendaDeMates tienda = new TiendaDeMates("Tienda Lunic", "Lunic S.A", "San Juan 24", admin);
    private static final Scanner sc = new Scanner(System.in);
    private static final ProductoDAO productoDao = new ProductoDAO();
    private static final UsuarioDao usuarioDao = new UsuarioDao();
    private static final PedidoDao pedidoDao = new PedidoDao();
    private static boolean volverMenuPrincipal = false;
    
    public static void main(String[] args) {
        admin.setTienda(tienda);
        ConexionDB.getInstancia(); // conexión SQLite
        int opcion = 0;

        try {
            do {
                mostrarMenuPrincipal();
                try {
                    opcion = sc.nextInt();
                    sc.nextLine();
                    switch (opcion) {
                        case 1 -> menuClienteNoRegistrado();
                        case 2 -> menuClienteRegistrado(iniciarSesion());
                        case 3 -> iniciarSesionAdministrador();
                        case 4 -> System.out.println("Saliendo del sistema...");
                        default -> System.out.println("Error: Seleccione una opción válida.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Error: Entrada no válida, debe ingresar un número.");
                    sc.nextLine();
                }
            } while (opcion != 4);
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    public static void mostrarMenuPrincipal() {
        System.out.println("==============================");
        System.out.println("Bienvenidos a Tienda Lunic");
        System.out.println("1. Acceder como Cliente");
        System.out.println("2. Acceder como Cliente VIP");
        System.out.println("3. Acceder como Administrador");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    //El cliente puede ver el catalogo, pero para comprar debe registrarse obligatoriamente
    public static void menuClienteNoRegistrado() throws Exception {
        int opc = 0;
        boolean salir = false;
        System.out.println("Usted ya está registrado? (S/N)");
        String respuesta;
        do {
            respuesta = sc.nextLine();
            if (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
                System.out.println("Ha ingresado un carácter inválido. Vuelva a intentar.");
            }
        } while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n"));
        if (respuesta.equalsIgnoreCase("s")) {
            menuClienteRegistrado(iniciarSesion());
        } else {
            do {
                do {
                    System.out.println("1. Registrarse");
                    System.out.println("2. Ver catálogo");
                    System.out.println("3. Volver al menú principal");
                    System.out.print("Seleccione una opción: ");
                    opc = sc.nextInt();
                    if (opc < 1 && opc > 3) {
                        System.out.println("Ha ingresado una opción inválida. Vuelva a intentar.");
                    }
                } while (opc < 1 && opc > 3);
                switch (opc) {
                    case 1: 
                        registrarCliente();
                        salir = true;
                        break;
                    case 2:
                        catalogo();
                        System.out.println("--- Para iniciar una compra, primero debe registrarse. ---");
                        break;
                    case 3:
                        salir = true;
                }
            } while (!salir);
            
        }
    }

    private static Usuario iniciarSesion(){
        Usuario usuario = null;
        Scanner asc = new Scanner(System.in);
        System.out.println("======================");
        System.out.println("Ingrese su email: ");
        String email= asc.nextLine().trim();
        try {
            usuario = usuarioDao.buscarPorEmail(email);
            if (usuario == null){
                System.out.println("No se encontró un usuario con ese email");
                System.out.println("Registrese.");  
            } else if (usuario instanceof ClienteVip clienteVip) {
                System.out.println("Ingrese su contraseña:");
                String contrasenia = sc.nextLine();
                if (!contrasenia.equals(clienteVip.getContrasenia())) {
                    System.out.println("Contraseña incorrecta.");
                    usuario = null;
                } else {
                    System.out.println("======================");
                    System.out.println("Iniciando sesión...");
                    System.out.println("Bienvenido/a de nuevo "+ usuario.getNombre()); 
                }
            } else {
                System.out.println("======================");
                System.out.println("Iniciando sesión...");
                System.out.println("Bienvenido/a de nuevo "+ usuario.getNombre()); 
            }       
        } catch (Exception e) {
            System.out.println("Error al buscar usuario;" + e.getMessage());
        }
        return usuario;
    }
    
    //Mismo metodo para cliente normal y cliente vip
    private static void registrarCliente() {
        sc.nextLine();
        String nombre, apellido, email, direccion, metodoDePago, telefono, dni, respuesta, contrasenia;
        int opcion;
        double cuota;
        Usuario usuario;
        System.out.println("=== INGRESO DE DATOS DEL CLIENTE ===");
        try {
            System.out.print("Ingrese su nombre: ");
            nombre = sc.nextLine().trim();//Trim es para borrar los espacios al principo y al final
            System.out.print("Ingrese su apellido: ");
            apellido = sc.nextLine();

            boolean emailValido=false;
            do {     
                System.out.print("Ingrese su email: ");
                email = sc.nextLine().trim();
                try {
                    if (usuarioDao.existeEmail(email)){
                        System.out.println("Ese email ya existe, ingrese nuevamente el email");
                    } else {
                        emailValido=true;
                    }
                } catch (Exception e) {
                    System.out.println("Error al verificar email"+ e.getMessage());
                }
            } while (!emailValido);
        
            System.out.print("Ingrese su dirección: ");
            direccion = sc.nextLine();

            System.out.print("Ingrese su teléfono: ");
            telefono = sc.nextLine();

            boolean dnivalido=false;
            do {            
                System.out.print("Ingrese su DNI: ");
                dni = sc.nextLine();
                try {
                    if (usuarioDao.existeDni(dni)) {
                        System.out.println("Error: ya existe un usuario con este DNI.");
                    } else {
                        dnivalido = true;  
                    }
                } catch (Exception e) {
                    System.out.println("Error al verificar dni "+ e.getMessage());
                }
            } while (!dnivalido);
            
            do {
                System.out.print("¿Desea ingresar como Usuario VIP? (S/N): ");
                respuesta = sc.nextLine().trim();
                if (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
                    System.out.println("Ha ingresado un carácter inválido. Vuelva a intentar.");
                }
            } while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n"));

            if (respuesta.equalsIgnoreCase("s")) {
                do { 
                    System.out.println("Seleccione su método de pago:\n1. Débito\n2. Crédito");
                    opcion = sc.nextInt();
                    if (opcion < 1 || opcion > 2) {
                        System.out.println("Ha ingresado una opción inválida. Vuelva a intentar");
                    }
                } while (opcion < 1 || opcion > 2);
                System.out.println("Ha realizado su pago con éxito.");
                sc.nextLine();
                System.out.print("Ingrese una contraseña para su cuenta: ");
                contrasenia = sc.nextLine();
                
                
                usuario = new ClienteVip( direccion,telefono,dni, nombre,apellido,email, 0,new CarritoDeCompra(), contrasenia);
                System.out.println("Cuenta registrada como cliente VIP.");
            } else {
                usuario = new Cliente(direccion,telefono,dni,nombre,apellido,email,0,new CarritoDeCompra());
                System.out.println("Cuenta registrada como cliente normal.");
            }

            // Guarda el usuario en la base de datos
            usuarioDao.agregar(usuario);
            menuClienteRegistrado(usuario);

        } catch (InputMismatchException e) {
            System.out.println("Error: Ingrese números válidos para Teléfono y DNI.");
            sc.nextLine(); // limpia el buffer para evitar saltos de lectura
        } catch (Exception e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
        }
    }
    
    public static void menuClienteRegistrado(Usuario usuario) throws Exception {
        int opc = 0;
        if (usuario == null) {
            return;
        }
        do {
            do { 
                if (volverMenuPrincipal) {
                    opc = 5;
                } else {
                    System.out.println("====== Menú de " + usuario.getNombre() + " =====");
                    System.out.println("1. Modificar dirección\n2. Modificar teléfono\n3. Cambiar de categoría\n4. Comprar\n5. Volver al menú principal\n6. Eliminar cuenta");
                    System.out.print("Seleccione una opción: ");
                    opc = sc.nextInt();
                    if (opc < 1 || opc > 6) {
                        System.out.println("Ha ingresado una opción inválida. Vuelva a intentar");
                    }
                }
            } while (opc < 1 || opc > 6);
            switch (opc) {
                case 1 -> modificarDireccion(usuario);
                case 2 -> modificarTelefono(usuario);
                case 3 -> cambiarCategoria(usuario);
                case 4 -> menuCompra(usuario);
                case 5 -> System.out.println("Volviendo al menú principal...");
                case 6 -> eliminarCuenta(usuario);
            }
        } while (opc != 5);
        volverMenuPrincipal = false;
    }
    
    public static void modificarTelefono(Usuario usuario) {
        sc.nextLine();
        try {
            System.out.print("Nuevo teléfono: ");
            String nuevoTelefono = sc.nextLine();
            sc.nextLine();
            if (nuevoTelefono.isEmpty()) {
                System.out.println("No se puede ingresar un teléfono vacío. El teléfono permanece sin modificaciones.");
            } else {
                UsuarioDao usuarioDao = new UsuarioDao();
                usuarioDao.modificar(usuario, nuevoTelefono);
                System.out.println("Teléfono modificado correctamente.");
            }
        } catch (DatosInvalidosException e) {
            System.out.println("Error de validacion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
    
    public static void modificarDireccion(Usuario usuario) {
        sc.nextLine();
        try {
            System.out.print("Nueva dirección: ");
            String nuevaDireccion = sc.nextLine();
            if (nuevaDireccion.isEmpty()) {
                System.out.println("No se puede ingresar una dirección vacía. La dirección permanece sin modificaciones.");
            } else {
                if (usuario instanceof Cliente cliente) {
                    cliente.setDireccion(nuevaDireccion);
                } else if (usuario instanceof ClienteVip clienteVip) {
                    clienteVip.setDireccion(nuevaDireccion);
                }
                UsuarioDao usuarioDao = new UsuarioDao();
                usuarioDao.modificar(usuario);
                System.out.println("Dirección modificada correctamente.");
            }
        } catch (DatosInvalidosException e) {
            System.out.println("Error de validacion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
    
    private static void cambiarCategoria(Usuario usuario) throws Exception {
        String respuesta = "";
        int opc = 0;
        sc.nextLine();
        System.out.println("El cliente VIP goza de increíbles descuentos en nuestra tienda y sus envíos tienen prioridad.");
        //Cliente VIP puede volver a ser comun
        if (usuario instanceof ClienteVip clienteVip) {
            do {
                System.out.println("Está seguro de abandonar su suscripción VIP? (S/N)");
                respuesta = sc.nextLine();
                if (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
                    System.out.println("Ha ingresado un carácter inválido. Vuelva a intentar");
                }
            } while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n"));
            if (respuesta.equalsIgnoreCase("s")) {
                Usuario usuarioComun = new Cliente(clienteVip.getDireccion(), clienteVip.getTelefono(), clienteVip.getDni(), clienteVip.getNombre(), clienteVip.getApellido(), clienteVip.getEmail(),0,new CarritoDeCompra());
                usuarioDao.eliminar(usuario.getId());
                usuarioDao.agregar(usuarioComun);
                System.out.println("Ha dejado de ser Cliente VIP.");
                volverMenuPrincipal = true;
            } else {
                return;
            }
        //Cliente comun puede volverse VIP
        } else if (usuario instanceof Cliente cliente) {
            System.out.println("Solo debe abonar un plan mensual de 5000 pesos.");
            do {
                System.out.println("Desea ser Cliente VIP? (S/N)");
                respuesta = sc.nextLine();
                if (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
                    System.out.println("Ha ingresado un carácter inválido. Vuelva a intentar");
                }
            } while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n"));
            if (respuesta.equalsIgnoreCase("s")) {
                do { 
                    System.out.println("Seleccione su método de pago:\n1. Débito\n2. Crédito");
                    opc = sc.nextInt();
                    if (opc < 1 || opc > 2) {
                        System.out.println("Ha ingresado una opción inválida. Vuelva a intentar");
                    }
                } while (opc < 1 || opc > 2);
                System.out.println("Ha realizado su pago con éxito.");
                sc.nextLine();
                do { 
                    System.out.println("Ingrese una contraseña para su cuenta:");
                    respuesta = sc.nextLine();
                    if (respuesta.isEmpty()) {
                        System.out.println("La contraseña no puede estar vacía. Vuelva a intentar");
                    }
                } while (respuesta.isEmpty());
                Usuario usuarioVip = new ClienteVip(cliente.getDireccion(), cliente.getTelefono(), cliente.getDni(), cliente.getNombre(), cliente.getApellido(), cliente.getEmail(),0,new CarritoDeCompra(), respuesta);
                usuarioDao.eliminar(usuario.getId());
                usuarioDao.agregar(usuarioVip);
                System.out.println("Disfrute los beneficios VIP!");
                volverMenuPrincipal = true;
            } else {
                return;
            }
        }
    }

    public static void eliminarCuenta(Usuario usuario) throws Exception {
        String respuesta = "";
        sc.nextLine();
        do {
            System.out.println("Está seguro que desea eliminar su cuenta? (S/N)");
            respuesta = sc.nextLine();
            if (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
                System.out.println("Ha ingresado un carácter inválido. Vuelva a intentar");
            }
        } while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n"));
        if (respuesta.equalsIgnoreCase("s")) {
            usuarioDao.eliminar(usuario.getId());
            volverMenuPrincipal = true;
            System.out.println("Su cuenta ha sido eliminada.");
        } else {
            return;
        }
    }
    
    private static void menuCompra(Usuario usuario) {
        int opc = 0;
        if (usuario == null) {
            return;
        }

        // Obtener el carrito según el tipo de usuario
        CarritoDeCompra carrito;
        if (usuario instanceof Cliente clienteNormal) {
            carrito = clienteNormal.getCarrito();
        } else if (usuario instanceof ClienteVip clienteVip) {
            carrito = clienteVip.getCarrito();
        } else {
            System.out.println("Error: tipo de usuario no reconocido.");
            return;
        }
        
        do {
            // Mostrar nombre del usuario de manera segura
            if (usuario.getNombre() != null) {
                System.out.println("===== Menú de compra para " + usuario.getNombre() + " =====");
            } else {
                System.out.println("===== Menú de compra para CLIENTE =====");
            }

            System.out.println("1. Ver catalogo");
            System.out.println("2. Agregar producto al carrito");
            System.out.println("3. Eliminar producto del carrito");
            System.out.println("4. Ver carrito");
            System.out.println("5. Finalizar compra");
            System.out.println("6. Volver al menu principal");
            System.out.println("Seleccione una opcion: ");

            try {
                opc = sc.nextInt();
                sc.nextLine(); // limpiar buffer
                switch (opc) {
                    case 1 -> catalogo();
                    case 2 -> agregarProductoACarrito(usuario);
                    case 3 -> eliminarProductoDeCarrito(usuario);
                    case 4 -> carrito.mostrarProductos();
                    case 5 -> finalizarCompra(usuario);
                    case 6 -> System.out.println("Volviendo al menu principal...");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: debe ingresar un numero.");
                sc.nextLine(); // limpiar buffer
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opc != 6);
    }


    private static void catalogo() throws Exception {
        System.out.println("=== Catálogo de productos ===");
        List<Producto> productos = productoDao.buscarTodo();

        if (productos.isEmpty()) {
            System.out.println("El catálogo esta vacio. El administrador debe agregar productos.");
            return;
        }

        for (Producto p : productos) {
            p.mostrarInfo();
        }
    }

    private static void agregarProductoACarrito(Usuario usuario) throws Exception {
        if (usuario instanceof Cliente) {
            int cantidad;
            try {
                System.out.print("Ingrese el ID del producto a agregar: ");
                int id = sc.nextInt();
                sc.nextLine();
                Producto p = productoDao.buscar(id);
            
                if (p == null) {
                    System.out.println("Producto no encontrado");
                    return;
                }
                System.out.print("Ingrese la cantidad a agregar: ");
                cantidad = sc.nextInt();
                if (cantidad <= 0) {
                    System.out.println("Cantidad invalida");
                    return;
                }
                ///modificacion del stock y mostrar la cantidad restante de stock
                if (p.getStock() >= cantidad) {
                    ((Cliente) usuario).agregarProducto(p, cantidad);
                // Actualizar stock correctamente
                    p.setStock(p.getStock() - cantidad);
                    productoDao.modificar(p);
                    System.out.println("Se agregaron " + cantidad + " unidades de " + p.getNombre() + " de " + p.getMaterial() + " al carrito");
                } else {
                    System.out.println("No hay suficiente stock disponible.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }  
    }
    
    private static void eliminarProductoDeCarrito(Usuario usuario) throws Exception {
        if (usuario instanceof Cliente) {
            System.out.print("Ingrese el item del producto a eliminar: ");
            int index = sc.nextInt();
            index = index - 1;
            if (index >= ((Cliente) usuario).getCarrito().getProductos().size() || index < 0) {
                System.out.println("Item de producto inválido. El carrito permanece sin cambios.");
            } else {
                Producto p = ((Cliente) usuario).getCarrito().getProductos().get(index);
                ((Cliente) usuario).eliminarProducto(p);
                Producto productoEncontrado = productoDao.buscar(p.getId());
                p.setStock(p.getStock() + productoEncontrado.getStock());
                productoDao.modificar(p);
                System.out.println("Se eliminaron todas las unidades de " + p.getNombre() + " de " + p.getMaterial());
            }
        }  
    }

    private static void finalizarCompra(Usuario usuario) throws Exception {
        int opc = 0;
        String numTarjeta;
        Pedido nuevoPedido;
        boolean envioADomicilio = false;
        UsuarioDao usuarioDao = new UsuarioDao();
        double totalBase, totalFinal;
        if (usuario == null) {
            System.out.println("Error: Usuario nulo.");
            return;
        }

        CarritoDeCompra carrito;
        if (usuario instanceof Cliente clienteNormal) {
            carrito = clienteNormal.getCarrito();
        } else if (usuario instanceof ClienteVip clienteVip) {
            carrito = clienteVip.getCarrito();
        } else {
            System.out.println("Error: tipo de usuario no reconocido.");
            return;
        }

        // Validar carrito
        if (carrito == null || carrito.estaVacio()) {
            System.out.println("El carrito está vacío. No se puede finalizar la compra.");
            return;
        }

        Scanner scs = new Scanner(System.in);

        // Elección de entrega
        do {
            System.out.println("Desea que el pedido sea enviado a su dirección o retirarlo por sucursal?");
            System.out.println("El envío supondrá un cargo extra de 3000 pesos.");
            System.out.println("1. Enviar a mi dirección\n2. Retiro por sucursal");
            opc = sc.nextInt();
            if (opc < 1 || opc > 2) {
                System.out.println("Ha ingresado una opción inválida. Vuelva a intentar");
            }
        } while (opc < 1 || opc > 2);
        
        // Calcular totales
        if (opc == 1) {
            totalBase = carrito.calcularTotal();
            totalFinal = totalBase+3000;
            envioADomicilio = true;
        } else {
            totalBase = carrito.calcularTotal();
            totalFinal = totalBase;
        }
        
        String mensajeDescuento = "";

        // Verificamos si el cliente es VIP
        if (usuario instanceof ClienteVip clienteVip) {
            totalFinal = clienteVip.calcularTotalConDescuento(totalFinal);
            mensajeDescuento = " (20% de descuento aplicado)";
        }

        // Mostrar resumen
        System.out.println("===================================");
        System.out.println("   FINALIZAR COMPRA == CLIENTE: " + usuario.getNombre().toUpperCase());
        System.out.println("===================================");
        System.out.println("Total base: $ " + String.format("%.2f", totalBase));//%.2f indica el numero que es un float y tambien solo muestra 2 digitos despues de la coma
        System.out.println("Total a pagar: $ " + String.format("%.2f", totalFinal) + mensajeDescuento);

        // Metodo de pago
        if (envioADomicilio) {
            do {
                System.out.println("Seleccione su método de pago:\n1. Débito\n2. Crédito");
                opc = sc.nextInt();
                if (opc < 1 || opc > 2) {
                    System.out.println("Ha ingresado una opción inválida. Vuelva a intentar");
                }
            } while (opc < 1 || opc > 2);
            sc.nextLine();
            // Acciones segun metodo de pago
            if (opc == 1) {
                do {
                    System.out.print("Ingrese los últimos 4 dígitos de su tarjeta de débito: ");
                    numTarjeta = sc.nextLine();
                    if (numTarjeta.length() != 4) {
                        System.out.println("Cantidad de números inválida. Vuelva a intentar.");
                    }
                } while (numTarjeta.length() != 4);
                System.out.println("Pago realizado con tarjeta terminada en " + numTarjeta + ".");
                nuevoPedido = new Pedido(usuario, new ArrayList<>(carrito.getProductos()), totalFinal, "Débito", "Pagado y Pendiente de envío");
            } else {
                do {
                    System.out.print("Ingrese los últimos 4 dígitos de su tarjeta de crédito: ");
                    numTarjeta = sc.nextLine();
                    if (numTarjeta.length() != 4) {
                        System.out.println("Cantidad de números inválida. Vuelva a intentar.");
                    }
                } while (numTarjeta.length() != 4);
                do {
                    System.out.print("Desea pagar en 1, 3 o 6 cuotas sin interés?: ");
                    opc = sc.nextInt();
                    if (opc != 1 && opc != 3 && opc != 6) {
                        System.out.println("Cantidad de cuotas inválida. Vuelva a intentar");
                    }
                } while (opc != 1 && opc != 3 && opc != 6);
                System.out.println("Pago realizado en " + opc + " cuotas sin interés.");
            }
            // Registrar pedido en la base de datos
            nuevoPedido = new Pedido(usuario, new ArrayList<>(carrito.getProductos()), totalFinal, "Crédito", "Pagado y Pendiente de envío");
        } else {
            do {
                System.out.println("Seleccione su método de pago:\n1. Efectivo\n2. Débito\n3. Crédito");
                opc = sc.nextInt();
                if (opc < 1 || opc > 3) {
                    System.out.println("Ha ingresado una opción inválida. Vuelva a intentar");
                }
            } while (opc < 1 || opc > 3);
            
            // Acciones segun metodo de pago
            if (opc == 1) {
                System.out.println("Usted eligió pagar en efectivo.");
                System.out.println("Por favor acérquese al local para pagar y retirar su compra.");
                // Registrar pedido en la base de datos
                nuevoPedido = new Pedido(usuario, new ArrayList<>(carrito.getProductos()), totalFinal, "Efectivo", "Pendiente de pago y Pendiente de retiro");
            } else if (opc == 2) {
                do {
                    System.out.print("Ingrese los últimos 4 dígitos de su tarjeta de débito: ");
                    numTarjeta = sc.nextLine();
                    if (numTarjeta.length() != 4) {
                        System.out.println("Cantidad de números inválida. Vuelva a intentar.");
                    }
                } while (numTarjeta.length() != 4);
                System.out.println("Pago realizado con tarjeta terminada en " + numTarjeta + ".");
                // Registrar pedido en la base de datos
                nuevoPedido = new Pedido(usuario, new ArrayList<>(carrito.getProductos()), totalFinal, "Débito","Pagado y Pendiente de retiro");
            } else {
                do {
                    System.out.print("Ingrese los últimos 4 dígitos de su tarjeta de crédito: ");
                    numTarjeta = sc.nextLine();
                    if (numTarjeta.length() != 4) {
                        System.out.println("Cantidad de números inválida. Vuelva a intentar.");
                    }
                } while (numTarjeta.length() != 4);
                do {
                    System.out.print("Desea pagar en 1, 3 o 6 cuotas sin interés?: ");
                    opc = sc.nextInt();
                    if (opc != 1 && opc != 3 && opc != 6) {
                        System.out.println("Cantidad de cuotas inválida. Vuelva a intentar");
                    }
                } while (opc != 1 && opc != 3 && opc != 6);
                System.out.println("Pago realizado en " + opc + " cuotas sin interés.");
                // Registrar pedido en la base de datos
                nuevoPedido = new Pedido(usuario, new ArrayList<>(carrito.getProductos()), totalFinal, "Crédito", "Pagado y Pendiente de retiro");
            }
        }
        

        pedidoDao.agregar(nuevoPedido);
        System.out.println("===================================");
        System.out.println("Pago confirmado correctamente");
        System.out.println("Su pedido ha sido procesado con éxito.");
        System.out.println("ID del pedido: " + nuevoPedido.getId());
        if (envioADomicilio) {
            System.out.println("Tiempo estimado de entrega: 3 días hábiles.");
        }
        System.out.println("===================================");

        // Vaciar carrito
        carrito.vaciarCarrito();

        //Mensaje final según tipo de cliente
        if (usuario instanceof ClienteVip) {
            System.out.println("Gracias por su compra, " + usuario.getNombre() + "\n Siga disfrutando su beneficio VIP!");
        } else {
            System.out.println("Gracias por su compra, " + usuario.getNombre() + " ");
            System.out.println("Si desea obtener descuentos, considere hacerse Cliente VIP.");
        }
    }

    private static void iniciarSesionAdministrador() throws Exception {
        System.out.println("=== Acceso Administrador ===");
        int intentos = 0;

        do {
            System.out.print("Ingrese la contraseña: ");
            try {
                int contrasenia = sc.nextInt();
                sc.nextLine();

                if (contrasenia == admin.getContrasenia()) {
                    System.out.println("Acceso correcto.");
                    menuAdministrador();
                    return;
                } else {
                    System.out.println("Contraseña incorrecta.");
                    intentos++;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: La contraseña debe ser númerica.");
                sc.nextLine();
                intentos++;
            }
        } while (intentos < 3);

        System.out.println("Demasiados intentos fallidos.\nRegresando al menu principal...");
    }

    private static void menuAdministrador() throws Exception {
        int opc;

        do {
            System.out.println("== Panel de Administrador ==");
            System.out.println("1. Modificar stock de un producto");
            System.out.println("2. Modificar precio de un producto");
            System.out.println("3. Agregar un producto");
            System.out.println("4. Eliminar un producto");
            System.out.println("5. Catálogo");
            System.out.println("6. Lista de clientes");
            System.out.println("7. Pedidos");
            System.out.println("8. Volver al menú principal");
            System.out.print("Seleccione una opcion: ");
            try {
                opc = sc.nextInt();
                sc.nextLine();
                switch (opc) {
                    case 1 -> modificarStock();
                    case 2 -> modificarPrecio();
                    case 3 -> agregarProducto();
                    case 4 -> eliminarProducto();
                    case 5 -> catalogo();
                    case 6 -> listaClientes();
                    case 7 -> menuPedidos();
                    case 8 -> System.out.println("Cerrando sesión en modo admin...");
                    default -> System.out.println("Opcion inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número.");
                sc.nextLine();
                opc = 0;
            }
        } while (opc != 8);
    }  
    
    private static void modificarStock() {
        try {
            System.out.print("ID del producto: ");
            int id = sc.nextInt();
            Producto p = productoDao.buscar(id);
            System.out.print("Nueva cantidad de stock: ");
            int nuevaCantidad = sc.nextInt();
            sc.nextLine();
            if (nuevaCantidad < 0) {
                System.out.println("No se puede ingresar una cantidad negativa. El producto permanece sin modificaciones.");
            } else {
                p.setStock(nuevaCantidad);
                admin.modificar(p);
                System.out.println("Stock modificado correctamente.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: los valores deben ser enteros.");
            sc.nextLine();
        } catch (DatosInvalidosException e) {
            System.out.println("Error de validacion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private static void modificarPrecio() {
        try {
            System.out.print("ID del producto: ");
            int id = sc.nextInt();
            System.out.print("Nuevo precio por unidad: ");
            double nuevoPrecio = sc.nextDouble();
            sc.nextLine();
            if (nuevoPrecio < 0) {
                System.out.println("El precio no puede ser negativo. El producto permanece sin modificaciones.");
            } else {
                admin.modificar(id, nuevoPrecio);
                System.out.println("Precio modificado correctamente.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Los valores deben ser númericos.");
            sc.nextLine();
        } catch (DatosInvalidosException e) {
            System.out.println("Error de validacion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }  
    
    public static void agregarProducto() {
        try {
            System.out.print("Nombre del producto: ");
            String nombre = sc.nextLine();
            System.out.print("Material del producto: ");
            String material = sc.nextLine();
            System.out.print("Color del producto: ");
            String color = sc.nextLine();
            System.out.print("Precio del producto: ");
            double precio = sc.nextDouble();
            System.out.print("Stock inicial: ");
            int stock = sc.nextInt();
            sc.nextLine();

            int idTemporal = 0;

            Producto producto = new Producto(material, color, nombre, stock, idTemporal, precio);
            admin.agregarProducto(producto);
            
        } catch (InputMismatchException e) {
            System.out.println("Error: ingrese números válidos para precio y stock.");
            sc.nextLine(); // limpiar buffer
        } catch (Exception e) {
            System.out.println("Error al agregar producto: " + e.getMessage());
        }
    }
    
    public static void eliminarProducto() {
        try {
            System.out.print("Ingrese el ID del producto a eliminar: ");
            int id = sc.nextInt();
            sc.nextLine(); // limpiar buffer
  
            Producto producto = productoDao.buscar(id);
            if (producto != null) {
                admin.eliminarProducto(id);
                System.out.println("Producto eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún producto con ese ID.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: el ID debe ser un número entero.");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }
    
    public static void listaClientes() throws Exception {
        System.out.println("=== CLIENTES REGISTRADOS ===");
        List<Usuario> clientes = usuarioDao.buscarTodo();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        for (Usuario u: clientes) {
            if (u instanceof Cliente clienteNormal) {
               clienteNormal.mostrarInfo();
            } else if (u instanceof ClienteVip clienteVip) {
                clienteVip.mostrarInfo();
            }
        }
    }
    
    public static void menuPedidos() throws Exception {
        int opc = 0;
        do {
            System.out.println("== Menú Pedidos ==");
            System.out.println("1. Lista de pedidos");
            System.out.println("2. Modificar estado de un pedido");
            System.out.println("3. Volver al panel del administrador");
            System.out.print("Seleccione una opcion: ");
            opc = sc.nextInt();
            if (opc < 1 || opc > 3) {
                System.out.println("Ha ingresado una opción inválida. Vuelva a intentar.");
            }
        } while (opc < 1 || opc > 3);
        switch (opc) {
            case 1 -> listaPedidos();
            case 2 -> modificarEstadoDelPedido();
            case 3 -> System.out.println("Regresando al panel de administrador...");
        }
        
    }
    
    public static void listaPedidos() throws Exception {
        System.out.println("=== PEDIDOS ===");
        List<Pedido> pedidos = pedidoDao.buscarTodo();
 
        
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }

        for (Pedido p: pedidos) {
            p.mostrarInfo();;
        }
    }
    
    public static void modificarEstadoDelPedido() {
        int opc = 0;
        try {
            System.out.print("ID del pedido: ");
            int id = sc.nextInt();
            do {
                System.out.println("Seleccione el estado del pedido: ");
                System.out.println("1. Entregado\n2. Cancelado"); 
                opc = sc.nextInt();
                if (opc < 1 || opc > 2) {
                    System.out.println("Ha ingresado una opción inválida. Vuelva a intentar.");
                }
            } while (opc < 1 || opc > 2);
            Pedido pedido = pedidoDao.buscar(id);
            if (pedido == null) {
                System.out.println("No existe un pedido con ese ID.");
                return;
            } else {
                if (opc == 1) {
                    pedido.setEstado("Entregado");
                    pedidoDao.modificar(pedido);
                } else {
                    pedidoDao.eliminar(id);
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Los valores deben ser númericos.");
            sc.nextLine();
        } catch (DatosInvalidosException e) {
            System.out.println("Error de validacion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

} 
