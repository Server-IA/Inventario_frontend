const normalize = (value) =>
  String(value ?? "")
    .trim()
    .toLocaleLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");

const renderName = (item) => item?.name ?? item?.nombre ?? item?.descripcion ?? "";

export const canSaveKardex = ({ savingKardex, loadingEditData }) =>
  !savingKardex && !loadingEditData;

export const shouldResetEditLoading = ({ open, articleModalOpen }) =>
  !open && !articleModalOpen;

export const resolveMovementTypeId = (movementTypes, movementTypeName) => {
  const target = normalize(movementTypeName);
  if (!target) return "";

  return (movementTypes || []).find((item) => normalize(renderName(item)) === target)?.id ?? "";
};
