export default function Header({title = ""}) {
  return (
    <div className="p-4 bg-blue-100/30">
      <span className="font-semibold">{title}</span>
    </div>
  );
}
