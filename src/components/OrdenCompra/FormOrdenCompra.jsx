// 🔥 ESTE ES EL FORMULARIO FINAL SIN LÓGICA DE “ENVIAR AL PROVEEDOR”
import * as React from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import StackButtons from "../StackButtons";
import { Formik, Form } from "formik";
import * as Yup from "yup";

/* ==================== Helpers ==================== */
const toArray = (data) =>
  Array.isArray(data)
    ? data
    : Array.isArray(data?.content)
    ? data.content
    : Array.isArray(data?.data)
    ? data.data
    : [];

const numberRequired = (msg) =>
  Yup.number()
    .transform((v, orig) =>
      orig === "" || orig === null ? undefined : Number(orig)
    )
    .typeError(msg)
    .required(msg);

/* ==================== Schema ==================== */
const OrdenCompraSchema = Yup.object().shape({
  fechaHora: Yup.string().required("La fecha es obligatoria."),
  pedidoId: numberRequired("El pedido es obligatorio."),
  proveedorId: numberRequired("El proveedor es obligatorio."),
  descripcion: Yup.string(),
  estadoId: numberRequired("El estado es obligatorio."),
});

const ESTADO_ACTIVO_ID = 23;

export default function FormOrdenCompra({
  setMessage,
  selectedRow,
  setSelectedRow,
  reloadData,
}) {
  const [open, setOpen] = React.useState(false);
  const [methodName, setMethodName] = React.useState("");
  const [pedidos, setPedidos] = React.useState([]);
  const [proveedores, setProveedores] = React.useState([]);
  const [estados, setEstados] = React.useState([]);

  /* ========== Acciones CRUD del toolbar ========== */
  const create = () => {
    setSelectedRow({
      id: 0,
      fechaHora: "",
      pedidoId: "",
      proveedorId: "",
      descripcion: "",
      estadoId: ESTADO_ACTIVO_ID, // siempre ACTIVA
    });
    setMethodName("Agregar");
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona una fila para actualizar",
      });
      return;
    }
    setMethodName("Actualizar");
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona una fila para eliminar",
      });
      return;
    }

    axios
      .delete(`/v1/orden-compra/${selectedRow.id}`)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: "Orden eliminada con éxito!",
        });
        reloadData();
      })
      .catch((error) => {
        setMessage({
          open: true,
          severity: "error",
          text: `Error al eliminar: ${error.message}`,
        });
      });
  };

  const handleClose = () => setOpen(false);

  /* ========== Cargar catálogos ========== */
  React.useEffect(() => {
    axios
      .get("/v1/items/pedido/0")
      .then((res) => setPedidos(toArray(res.data)))
      .catch(() => setPedidos([]));

    axios
      .get("/v1/items/proveedor/0")
      .then((res) => setProveedores(toArray(res.data)))
      .catch(() => setProveedores([]));

    axios
      .get("/v1/items/orden_compra_estado/0")
      .then((res) => setEstados(toArray(res.data)))
      .catch(() => setEstados([]));
  }, []);

  const getEstadoLabel = (id) => {
    if (!id) return "";
    const idNum = Number(id);
    const found = estados.find((e) => Number(e.id) === idNum);
    return (
      found?.nombre ||
      found?.name ||
      found?.descripcion ||
      (idNum === ESTADO_ACTIVO_ID ? "Activo" : `Estado ${idNum}`)
    );
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />

      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <Formik
          initialValues={{
            fechaHora: selectedRow?.fechaHora || "",
            pedidoId: selectedRow?.pedidoId ?? "",
            proveedorId: selectedRow?.proveedorId ?? "",
            descripcion: selectedRow?.descripcion || "",
            estadoId:
              selectedRow?.estadoId ??
              (methodName === "Agregar" ? ESTADO_ACTIVO_ID : ""),
          }}
          enableReinitialize
          validationSchema={OrdenCompraSchema}
          onSubmit={(values, { setSubmitting }) => {
            const id = selectedRow?.id || 0;

            const url =
              methodName === "Agregar"
                ? "/v1/orden-compra" // <- YA SIN createOrdenCompraNUEVO
                : `/v1/orden-compra/${id}`;

            const method = methodName === "Agregar" ? axios.post : axios.put;

            let payload = {
              ...values,
              pedidoId: Number(values.pedidoId),
              proveedorId: Number(values.proveedorId),
              estadoId:
                methodName === "Agregar"
                  ? ESTADO_ACTIVO_ID
                  : Number(values.estadoId),
            };

            method(url, payload)
              .then(() => {
                setMessage({
                  open: true,
                  severity: "success",
                  text: `Orden ${
                    methodName === "Agregar" ? "creada" : "actualizada"
                  } con éxito!`,
                });
                setSubmitting(false);
                setOpen(false);
                reloadData();
              })
              .catch((error) => {
                const msg =
                  error.response?.data?.message || error.message;
                setMessage({
                  open: true,
                  severity: "error",
                  text: `Error al guardar: ${msg}`,
                });
                setSubmitting(false);
              });
          }}
        >
          {({
            values,
            errors,
            touched,
            handleChange,
            setFieldValue,
            isSubmitting,
          }) => (
            <Form>
              <DialogTitle>{methodName} Orden de Compra</DialogTitle>

              <DialogContent>
                <DialogContentText>Completa el formulario.</DialogContentText>

                <FormControl fullWidth margin="normal">
                  <TextField
                    name="fechaHora"
                    label="Fecha y Hora"
                    type="datetime-local"
                    InputLabelProps={{ shrink: true }}
                    value={values.fechaHora}
                    onChange={handleChange}
                    error={touched.fechaHora && Boolean(errors.fechaHora)}
                    helperText={touched.fechaHora && errors.fechaHora}
                  />
                </FormControl>

                <FormControl
                  fullWidth
                  margin="normal"
                  error={touched.pedidoId && Boolean(errors.pedidoId)}
                >
                  <InputLabel id="pedidoId-label">Pedido</InputLabel>
                  <Select
                    labelId="pedidoId-label"
                    name="pedidoId"
                    label="Pedido"
                    value={values.pedidoId}
                    onChange={(e) =>
                      setFieldValue("pedidoId", Number(e.target.value))
                    }
                  >
                    {pedidos.map((p) => (
                      <MenuItem key={p.id} value={p.id}>
                        {p.descripcion ?? `Pedido ${p.id}`}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>

                <FormControl
                  fullWidth
                  margin="normal"
                  error={touched.proveedorId && Boolean(errors.proveedorId)}
                >
                  <InputLabel id="proveedorId-label">Proveedor</InputLabel>
                  <Select
                    labelId="proveedorId-label"
                    name="proveedorId"
                    label="Proveedor"
                    value={values.proveedorId}
                    onChange={(e) =>
                      setFieldValue("proveedorId", Number(e.target.value))
                    }
                  >
                    {proveedores.map((p) => (
                      <MenuItem key={p.id} value={p.id}>
                        {p.name ?? p.nombre ?? `Proveedor ${p.id}`}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>

                <FormControl fullWidth margin="normal">
                  <TextField
                    name="descripcion"
                    label="Descripción"
                    value={values.descripcion}
                    onChange={handleChange}
                  />
                </FormControl>

                {/* Estado solo lectura en crear */}
                {methodName === "Agregar" ? (
                  <FormControl fullWidth margin="normal">
                    <TextField
                      label="Estado"
                      value={getEstadoLabel(ESTADO_ACTIVO_ID)}
                      InputProps={{ readOnly: true }}
                    />
                  </FormControl>
                ) : (
                  <FormControl fullWidth margin="normal">
                    <InputLabel id="estadoId-label">Estado</InputLabel>
                    <Select
                      labelId="estadoId-label"
                      name="estadoId"
                      label="Estado"
                      value={values.estadoId}
                      onChange={(e) =>
                        setFieldValue("estadoId", Number(e.target.value))
                      }
                    >
                      {estados.map((e) => (
                        <MenuItem key={e.id} value={e.id}>
                          {e.nombre ?? e.name ?? `Estado ${e.id}`}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}
              </DialogContent>

              <DialogActions>
                <Button onClick={handleClose} disabled={isSubmitting}>
                  Cancelar
                </Button>
                <Button type="submit" disabled={isSubmitting}>
                  {methodName}
                </Button>
              </DialogActions>
            </Form>
          )}
        </Formik>
      </Dialog>
    </>
  );
}

FormOrdenCompra.propTypes = {
  setMessage: PropTypes.func.isRequired,
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
};
