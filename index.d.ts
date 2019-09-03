declare module 'react-native-floating-bubble' {
	export type title = {
		title: string;
		color?: string;
	};
	export type buttonItem = {
		title: string;
		value: string;
		color?: string;
	};

	export function showFloatingBubble(
		titleData: title,
		data: buttonItem[]
	): Promise<string>;
	export function hideFloatingBubble(): Promise<string>;
	export function checkPermission(): Promise<boolean | string>;
	export function requestPermission(): Promise<Promise>;
	export function initialize(): Promise<string>;
}
